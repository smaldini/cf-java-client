/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.operations.applications;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.applications.ApplicationInstanceInfo;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesRequest;
import org.cloudfoundry.client.v2.applications.ApplicationInstancesResponse;
import org.cloudfoundry.client.v2.applications.ApplicationResource;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsRequest;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsResponse;
import org.cloudfoundry.client.v2.applications.SummaryApplicationRequest;
import org.cloudfoundry.client.v2.applications.SummaryApplicationResponse;
import org.cloudfoundry.client.v2.applications.UpdateApplicationRequest;
import org.cloudfoundry.client.v2.routes.Route;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryRequest;
import org.cloudfoundry.client.v2.spaces.GetSpaceSummaryResponse;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsRequest;
import org.cloudfoundry.client.v2.spaces.ListSpaceApplicationsResponse;
import org.cloudfoundry.client.v2.spaces.SpaceApplicationSummary;
import org.cloudfoundry.client.v2.stacks.GetStackRequest;
import org.cloudfoundry.client.v2.stacks.GetStackResponse;
import org.cloudfoundry.client.v2.stacks.ListStacksRequest;
import org.cloudfoundry.client.v2.stacks.ListStacksResponse;
import org.cloudfoundry.operations.util.Dates;
import org.cloudfoundry.operations.util.Exceptions;
import org.cloudfoundry.operations.util.Function2;
import org.cloudfoundry.operations.util.Function4;
import org.cloudfoundry.operations.util.Optional;
import org.cloudfoundry.operations.util.Optionals;
import org.cloudfoundry.operations.util.Tuples;
import org.cloudfoundry.operations.util.Validators;
import org.cloudfoundry.operations.util.v2.Paginated;
import org.cloudfoundry.operations.util.v2.Resources;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.fn.Function;
import reactor.fn.tuple.Tuple;
import reactor.fn.tuple.Tuple2;
import reactor.fn.tuple.Tuple4;
import reactor.rx.Stream;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class DefaultApplications implements Applications {

    private final CloudFoundryClient cloudFoundryClient;

    private final Mono<String> spaceId;

    public DefaultApplications(CloudFoundryClient cloudFoundryClient, Mono<String> spaceId) {
        this.cloudFoundryClient = cloudFoundryClient;
        this.spaceId = spaceId;
    }

    @Override
    public Mono<ApplicationDetail> get(GetApplicationRequest request) {
        return Validators
                .validate(request)
                .and(this.spaceId)
                .then(requestApplicationResource(this.cloudFoundryClient))
                .then(gatherApplicationInfo(this.cloudFoundryClient))
                .map(toApplicationDetail());
    }

    @Override
    public Publisher<ApplicationSummary> list() {
        return this.spaceId
                .then(requestSpaceSummary(this.cloudFoundryClient))
                .flatMap(extractApplications())
                .map(toApplication());
    }

    @Override
    public Mono<Void> push(PushApplicationRequest request) {
        Validators
                .validate(request)
                .then(requestStackId(this.cloudFoundryClient))
                .then(createOrUpdateApplication(this.cloudFoundryClient, this.spaceId));


        // TODO
        return null;
    }

    private static Function<Tuple2<Optional<String>, PushApplicationRequest>, Mono<Tuple2<String, PushApplicationRequest>>> createOrUpdateApplication(final CloudFoundryClient cloudFoundryClient,
                                                                                                                                                      final Mono<String> spaceId) {
        return new Function<Tuple2<Optional<String>, PushApplicationRequest>, Mono<Tuple2<String, PushApplicationRequest>>>() {

            @Override
            public Mono<Tuple2<String, PushApplicationRequest>> apply(Tuple2<Optional<String>, PushApplicationRequest> tuple) {
                Optional<String> stackId = tuple.t1;
                PushApplicationRequest pushApplicationRequest = tuple.t2;

                return spaceId
                        .then(requestGetApplicationId(cloudFoundryClient, pushApplicationRequest.getName()))
                        .then(requestUpdateApplicationId(cloudFoundryClient))
                        //   .otherwiseIfEmpty(requestCreateApplicationId)
                        .and(Mono.just(pushApplicationRequest));
            }

        };
    }

    private static Function<GetSpaceSummaryResponse, Stream<SpaceApplicationSummary>> extractApplications() {
        return new Function<GetSpaceSummaryResponse, Stream<SpaceApplicationSummary>>() {

            @Override
            public Stream<SpaceApplicationSummary> apply(GetSpaceSummaryResponse getSpaceSummaryResponse) {
                return Stream.fromIterable(getSpaceSummaryResponse.getApplications());
            }

        };
    }

    private static Function<ApplicationResource, Mono<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>>>
    gatherApplicationInfo(final CloudFoundryClient cloudFoundryClient) {
        return new Function<ApplicationResource, Mono<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>>>() {

            @Override
            public Mono<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>> apply(ApplicationResource applicationResource) {
                String applicationId = Resources.getId(applicationResource);
                String stackId = Resources.getEntity(applicationResource).getStackId();

                return Mono.when(requestApplicationStats(cloudFoundryClient, applicationId), requestApplicationSummary(cloudFoundryClient, applicationId), requestStack(cloudFoundryClient, stackId),
                        requestApplicationInstances(cloudFoundryClient, applicationId));
            }

        };
    }

    private static String getBuildpack(SummaryApplicationResponse response) {
        return Optional
                .ofNullable(response.getBuildpack())
                .orElse(response.getDetectedBuildpack());
    }

    private static Mono<ApplicationInstancesResponse> requestApplicationInstances(CloudFoundryClient cloudFoundryClient, String applicationId) {
        ApplicationInstancesRequest request = ApplicationInstancesRequest.builder()
                .applicationId(applicationId)
                .build();

        return cloudFoundryClient.applicationsV2().instances(request);
    }

    private static Function<Tuple2<GetApplicationRequest, String>, Mono<ApplicationResource>> requestApplicationResource(final CloudFoundryClient cloudFoundryClient) {
        return Tuples.function(new Function2<GetApplicationRequest, String, Mono<ApplicationResource>>() {

            @Override
            public Mono<ApplicationResource> apply(GetApplicationRequest getApplicationRequest, String spaceId) {
                return Paginated
                        .requestResources(requestListApplicationsPage(cloudFoundryClient, getApplicationRequest.getName(), spaceId))
                        .single();
            }

        });
    }

    private static Mono<ApplicationStatisticsResponse> requestApplicationStats(CloudFoundryClient cloudFoundryClient, String applicationId) {
        ApplicationStatisticsRequest request = ApplicationStatisticsRequest.builder()
                .applicationId(applicationId)
                .build();

        return cloudFoundryClient.applicationsV2().statistics(request);
    }

    private static Mono<SummaryApplicationResponse> requestApplicationSummary(CloudFoundryClient cloudFoundryClient, String applicationId) {
        SummaryApplicationRequest request = SummaryApplicationRequest.builder()
                .applicationId(applicationId)
                .build();

        return cloudFoundryClient.applicationsV2().summary(request);
    }

    private static Function<String, Mono<String>> requestGetApplicationId(final CloudFoundryClient cloudFoundryClient, final String name) {
        return new Function<String, Mono<String>>() {

            @Override
            public Mono<String> apply(String spaceId) {
                return Paginated
                        .requestResources(requestListApplicationsPage(cloudFoundryClient, name, spaceId))
                        .singleOrEmpty()
                        .map(Resources.extractId());
            }

        };
    }

    private static Function<Integer, Mono<ListSpaceApplicationsResponse>> requestListApplicationsPage(final CloudFoundryClient cloudFoundryClient, final String name, final String spaceId) {
        return new Function<Integer, Mono<ListSpaceApplicationsResponse>>() {

            @Override
            public Mono<ListSpaceApplicationsResponse> apply(Integer page) {
                ListSpaceApplicationsRequest request = ListSpaceApplicationsRequest.builder()
                        .spaceId(spaceId)
                        .name(name)
                        .page(page)
                        .build();

                return cloudFoundryClient.spaces().listApplications(request);
            }

        };
    }

    private static Function<Integer, Mono<ListStacksResponse>> requestListStackPage(final CloudFoundryClient cloudFoundryClient, final String stack) {
        return new Function<Integer, Mono<ListStacksResponse>>() {

            @Override
            public Mono<ListStacksResponse> apply(Integer page) {
                ListStacksRequest request = ListStacksRequest.builder()
                        .name(stack)
                        .page(page)
                        .build();

                return cloudFoundryClient.stacks().list(request);
            }

        };
    }

    private static Function<String, Mono<GetSpaceSummaryResponse>> requestSpaceSummary(final CloudFoundryClient cloudFoundryClient) {
        return new Function<String, Mono<GetSpaceSummaryResponse>>() {

            @Override
            public Mono<GetSpaceSummaryResponse> apply(String targetedSpace) {
                GetSpaceSummaryRequest request = GetSpaceSummaryRequest.builder()
                        .spaceId(targetedSpace)
                        .build();

                return cloudFoundryClient.spaces().getSummary(request);
            }

        };
    }

    private static Mono<GetStackResponse> requestStack(CloudFoundryClient cloudFoundryClient, String stackId) {
        GetStackRequest request = GetStackRequest.builder()
                .stackId(stackId)
                .build();

        return cloudFoundryClient.stacks().get(request);
    }

    private static Function<PushApplicationRequest, Mono<Tuple2<Optional<String>, PushApplicationRequest>>> requestStackId(final CloudFoundryClient cloudFoundryClient) {
        return new Function<PushApplicationRequest, Mono<Tuple2<Optional<String>, PushApplicationRequest>>>() {

            @Override
            public Mono<Tuple2<Optional<String>, PushApplicationRequest>> apply(PushApplicationRequest pushApplicationRequest) {
                String stack = pushApplicationRequest.getStack();

                if (stack == null) {
                    return Mono.just(Tuple.of(Optional.<String>empty(), pushApplicationRequest));
                }

                return Paginated
                        .requestResources(requestListStackPage(cloudFoundryClient, stack))
                        .single()
                        .map(Resources.extractId())
                        .map(Optionals.<String>toOptional())
                        .and(Mono.just(pushApplicationRequest))
                        .otherwise(Exceptions.<Tuple2<Optional<String>, PushApplicationRequest>>convert("Stack %s does not exist", stack));
            }

        };
    }

    private static Function<String, Mono<String>> requestUpdateApplicationId(CloudFoundryClient cloudFoundryClient, final PushApplicationRequest pushApplicationRequest) {
        return new Function<String, Mono<String>>() {

            @Override
            public Mono<String> apply(String applicationId) {
                UpdateApplicationRequest request = UpdateApplicationRequest.builder()
                        .buildpack(pushApplicationRequest.getBuildpack())
                        .command(pushApplicationRequest.getCommand())
                        .diskQuota(pushApplicationRequest.getDiskLimit())
                        .environmentJson()
                        .healthCheckType()
                        .id(applicationId)
                        .instances()
                        .memory()
                        .stackId()
                        .state()
                        .build();

                // TODO

//                .diego(pushApplicationRequest.getDockerImage() != null)
//                .dockerImage()

                return null;
            }

        };
    }

    private static Function<SpaceApplicationSummary, ApplicationSummary> toApplication() {
        return new Function<SpaceApplicationSummary, ApplicationSummary>() {

            @Override
            public ApplicationSummary apply(SpaceApplicationSummary spaceApplicationSummary) {
                return ApplicationSummary.builder()
                        .diskQuota(spaceApplicationSummary.getDiskQuota())
                        .id(spaceApplicationSummary.getId())
                        .instances(spaceApplicationSummary.getInstances())
                        .memoryLimit(spaceApplicationSummary.getMemory())
                        .name(spaceApplicationSummary.getName())
                        .requestedState(spaceApplicationSummary.getState())
                        .runningInstances(spaceApplicationSummary.getRunningInstances())
                        .urls(spaceApplicationSummary.getUrls())
                        .build();
            }

        };
    }

    private static Function<Tuple4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse>, ApplicationDetail> toApplicationDetail() {
        return Tuples.function(new Function4<ApplicationStatisticsResponse, SummaryApplicationResponse, GetStackResponse, ApplicationInstancesResponse, ApplicationDetail>() {

            @Override
            public ApplicationDetail apply(ApplicationStatisticsResponse applicationStatisticsResponse, SummaryApplicationResponse summaryApplicationResponse, GetStackResponse getStackResponse,
                                           ApplicationInstancesResponse applicationInstancesResponse) {

                List<String> urls = toUrls(summaryApplicationResponse.getRoutes());

                return ApplicationDetail.builder()
                        .id(summaryApplicationResponse.getId())
                        .diskQuota(summaryApplicationResponse.getDiskQuota())
                        .memoryLimit(summaryApplicationResponse.getMemory())
                        .requestedState(summaryApplicationResponse.getState())
                        .instances(summaryApplicationResponse.getInstances())
                        .urls(urls)
                        .lastUploaded(toDate(summaryApplicationResponse.getPackageUpdatedAt()))
                        .stack(getStackResponse.getEntity().getName())
                        .buildpack(getBuildpack(summaryApplicationResponse))
                        .instanceDetails(toInstanceDetailList(applicationInstancesResponse, applicationStatisticsResponse))
                        .build();
            }

        });
    }

    private static Date toDate(String date) {
        if (date == null) {
            return null;
        }

        try {
            return Dates.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static Date toDate(Double date) {
        if (date == null) {
            return null;
        }

        return new Date(TimeUnit.SECONDS.toMillis(date.longValue()));
    }

    private static ApplicationDetail.InstanceDetail toInstanceDetail(Map.Entry<String, ApplicationInstanceInfo> entry, ApplicationStatisticsResponse statisticsResponse) {
        ApplicationStatisticsResponse.InstanceStats.Statistics stats = statisticsResponse.get(entry.getKey()).getStatistics();
        ApplicationStatisticsResponse.InstanceStats.Statistics.Usage usage = stats.getUsage();

        return ApplicationDetail.InstanceDetail.builder()
                .state(entry.getValue().getState())
                .since(toDate(entry.getValue().getSince()))
                .cpu(usage.getCpu())
                .memoryUsage(usage.getMemory())
                .diskUsage(usage.getDisk())
                .diskQuota(stats.getDiskQuota())
                .memoryQuota(stats.getMemoryQuota())
                .build();
    }

    private static List<ApplicationDetail.InstanceDetail> toInstanceDetailList(ApplicationInstancesResponse instancesResponse, ApplicationStatisticsResponse statisticsResponse) {
        List<ApplicationDetail.InstanceDetail> instanceDetails = new ArrayList<>(instancesResponse.size());

        for (Map.Entry<String, ApplicationInstanceInfo> entry : instancesResponse.entrySet()) {
            instanceDetails.add(toInstanceDetail(entry, statisticsResponse));
        }

        return instanceDetails;
    }

    private static List<String> toUrls(List<Route> routes) {
        List<String> urls = new ArrayList<>(routes.size());

        for (Route route : routes) {
            String hostName = route.getHost();
            String domainName = route.getDomain().getName();

            urls.add(hostName.isEmpty() ? domainName : String.format("%s.%s", hostName, domainName));
        }

        return urls;
    }

}
