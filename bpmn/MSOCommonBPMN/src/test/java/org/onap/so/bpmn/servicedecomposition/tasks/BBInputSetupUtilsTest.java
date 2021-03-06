/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2020 Nokia
 * ================================================================================
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
 * ============LICENSE_END=========================================================
 */

package org.onap.so.bpmn.servicedecomposition.tasks;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.onap.aai.domain.yang.CloudRegion;
import org.onap.aai.domain.yang.Configuration;
import org.onap.aai.domain.yang.Configurations;
import org.onap.aai.domain.yang.GenericVnf;
import org.onap.aai.domain.yang.GenericVnfs;
import org.onap.aai.domain.yang.InstanceGroup;
import org.onap.aai.domain.yang.L3Network;
import org.onap.aai.domain.yang.L3Networks;
import org.onap.aai.domain.yang.ServiceInstance;
import org.onap.aai.domain.yang.ServiceInstances;
import org.onap.aai.domain.yang.VolumeGroup;
import org.onap.aai.domain.yang.VolumeGroups;
import org.onap.aai.domain.yang.VpnBinding;
import org.onap.so.bpmn.common.InjectionHelper;
import org.onap.so.bpmn.servicedecomposition.bbobjects.Customer;
import org.onap.so.bpmn.servicedecomposition.bbobjects.ServiceSubscription;
import org.onap.so.bpmn.servicedecomposition.entities.ExecuteBuildingBlock;
import org.onap.so.bpmn.servicedecomposition.tasks.exceptions.MultipleObjectsFoundException;
import org.onap.so.bpmn.servicedecomposition.tasks.exceptions.NoServiceInstanceFoundException;
import org.onap.so.client.aai.AAIObjectPlurals;
import org.onap.so.client.aai.AAIObjectType;
import org.onap.so.client.aai.AAIResourcesClient;
import org.onap.so.client.aai.entities.uri.AAIResourceUri;
import org.onap.so.client.aai.entities.uri.AAIUriFactory;
import org.onap.so.client.graphinventory.entities.uri.Depth;
import org.onap.so.db.catalog.beans.VnfcInstanceGroupCustomization;
import org.onap.so.db.catalog.beans.CollectionResourceInstanceGroupCustomization;
import org.onap.so.db.catalog.beans.Service;
import org.onap.so.db.catalog.client.CatalogDbClient;
import org.onap.so.db.request.beans.InfraActiveRequests;
import org.onap.so.db.request.beans.RequestProcessingData;
import org.onap.so.db.request.client.RequestsDbClient;
import org.onap.so.serviceinstancebeans.CloudConfiguration;
import org.onap.so.serviceinstancebeans.RequestDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class BBInputSetupUtilsTest {
    protected ObjectMapper mapper = new ObjectMapper();
    private static final String RESOURCE_PATH = "src/test/resources/__files/ExecuteBuildingBlock/";

    @InjectMocks
    BBInputSetupUtils bbInputSetupUtils = new BBInputSetupUtils();

    @Mock
    protected CatalogDbClient MOCK_catalogDbClient;

    @Mock
    protected RequestsDbClient MOCK_requestsDbClient;

    @Mock
    protected AAIResourcesClient MOCK_aaiResourcesClient;

    @Mock
    protected InjectionHelper MOCK_injectionHelper;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        doReturn(MOCK_aaiResourcesClient).when(MOCK_injectionHelper).getAaiClient();
    }

    @Test
    public void getCatalogServiceByModelUUIDTest() throws IOException {
        Service expected = mapper.readValue(new File(RESOURCE_PATH + "CatalogServiceExpected.json"), Service.class);
        final String modelUUID = "modelUUIDTest";

        doReturn(expected).when(MOCK_catalogDbClient).getServiceByID(modelUUID);

        assertThat(bbInputSetupUtils.getCatalogServiceByModelUUID(modelUUID), sameBeanAs(expected));
    }

    @Test
    public void getCatalogServiceByModelVersionAndModelInvariantUUIDTest() throws IOException {
        final String modelVersion = "modelVersionTest";
        final String modelInvariantUUID = "modelInvariantUUIDTest";
        Service expectedService =
                mapper.readValue(new File(RESOURCE_PATH + "CatalogServiceExpected.json"), Service.class);

        doReturn(expectedService).when(MOCK_catalogDbClient).getServiceByModelVersionAndModelInvariantUUID(modelVersion,
                modelInvariantUUID);

        assertThat(bbInputSetupUtils.getCatalogServiceByModelVersionAndModelInvariantUUID(modelVersion,
                modelInvariantUUID), sameBeanAs(expectedService));
    }

    @Test
    public void getVnfcInstanceGroupsTest() throws IOException {
        final String modelCustomizationUUID = "modelCustomizationUUIDTest";
        VnfcInstanceGroupCustomization vnfc = mapper.readValue(
                new File(RESOURCE_PATH + "VnfcInstanceGroupCustomization.json"), VnfcInstanceGroupCustomization.class);

        doReturn(Arrays.asList(vnfc)).when(MOCK_catalogDbClient)
                .getVnfcInstanceGroupsByVnfResourceCust(modelCustomizationUUID);

        assertThat(bbInputSetupUtils.getVnfcInstanceGroups(modelCustomizationUUID), sameBeanAs(Arrays.asList(vnfc)));
    }

    @Test
    public void getRequestDetailsTest() throws IOException {
        final String requestId = "requestId";
        InfraActiveRequests infraActiveRequest = mapper
                .readValue(new File(RESOURCE_PATH + "InfraActiveRequestExpected.json"), InfraActiveRequests.class);
        RequestDetails expected =
                mapper.readValue(new File(RESOURCE_PATH + "RequestDetailsExpected.json"), RequestDetails.class);

        doReturn(infraActiveRequest).when(MOCK_requestsDbClient).getInfraActiveRequestbyRequestId(requestId);

        assertThat(bbInputSetupUtils.getRequestDetails(requestId), sameBeanAs(expected));
    }

    @Test
    public void getRequestDetailsNullTest() throws IOException {
        assertNull(bbInputSetupUtils.getRequestDetails(""));
    }

    @Test
    public void getCloudRegionTest() {
        CloudConfiguration cloudConfig = new CloudConfiguration();
        cloudConfig.setLcpCloudRegionId("lcpCloudRegionId");
        Optional<CloudRegion> expected = Optional.of(new CloudRegion());

        doReturn(expected).when(MOCK_aaiResourcesClient).get(CloudRegion.class,
                AAIUriFactory.createResourceUri(AAIObjectType.CLOUD_REGION, cloudConfig.getCloudOwner(),
                        cloudConfig.getLcpCloudRegionId()).depth(Depth.TWO));

        assertThat(bbInputSetupUtils.getCloudRegion(cloudConfig), sameBeanAs(expected.get()));
    }

    @Test
    public void getCloudRegionNullTest() {
        CloudConfiguration cloudConfig = new CloudConfiguration();
        cloudConfig.setLcpCloudRegionId("lcpCloudRegionId");

        assertNull(bbInputSetupUtils.getCloudRegion(cloudConfig));
    }

    @Test
    public void getCloudRegionEmptyIdTest() {
        CloudConfiguration cloudConfig = new CloudConfiguration();
        cloudConfig.setLcpCloudRegionId("");

        assertNull(bbInputSetupUtils.getCloudRegion(cloudConfig));
    }

    @Test
    public void getAAIInstanceGroupTest() {
        final String instanceGroupId = "instanceGroupId";
        Optional<InstanceGroup> expected = Optional.of(new InstanceGroup());
        expected.get().setId(instanceGroupId);

        doReturn(expected).when(MOCK_aaiResourcesClient).get(InstanceGroup.class,
                AAIUriFactory.createResourceUri(AAIObjectType.INSTANCE_GROUP, instanceGroupId));

        assertThat(bbInputSetupUtils.getAAIInstanceGroup(instanceGroupId), sameBeanAs(expected.get()));
    }

    @Test
    public void getAAIInstanceGroupNullTest() {
        assertNull(bbInputSetupUtils.getAAIInstanceGroup(""));
    }

    @Test
    public void getAAICustomerTest() {
        final String globalSubscriberId = "globalSubscriberId";
        Optional<org.onap.aai.domain.yang.Customer> expected = Optional.of(new org.onap.aai.domain.yang.Customer());
        expected.get().setGlobalCustomerId(globalSubscriberId);

        doReturn(expected).when(MOCK_aaiResourcesClient).get(org.onap.aai.domain.yang.Customer.class,
                AAIUriFactory.createResourceUri(AAIObjectType.CUSTOMER, globalSubscriberId));

        assertThat(bbInputSetupUtils.getAAICustomer(globalSubscriberId), sameBeanAs(expected.get()));
    }

    @Test
    public void getAAICustomerNullTest() {
        assertNull(bbInputSetupUtils.getAAICustomer(""));
    }

    @Test
    public void getAAIServiceSubscriptionTest() {
        final String globalSubscriberId = "globalSubscriberId";
        final String subscriptionServiceType = "subscriptionServiceType";
        Optional<org.onap.aai.domain.yang.ServiceSubscription> expected =
                Optional.of(new org.onap.aai.domain.yang.ServiceSubscription());

        expected.get().setServiceType(subscriptionServiceType);
        doReturn(expected).when(MOCK_aaiResourcesClient).get(org.onap.aai.domain.yang.ServiceSubscription.class,
                AAIUriFactory.createResourceUri(AAIObjectType.SERVICE_SUBSCRIPTION, globalSubscriberId,
                        subscriptionServiceType));

        assertThat(bbInputSetupUtils.getAAIServiceSubscription(globalSubscriberId, subscriptionServiceType),
                sameBeanAs(expected.get()));
    }

    @Test
    public void getAAIServiceSubscriptionErrorsTest() {
        assertNull(bbInputSetupUtils.getAAIServiceSubscription(null, null));
        assertNull(bbInputSetupUtils.getAAIServiceSubscription("", ""));
        assertNull(bbInputSetupUtils.getAAIServiceSubscription("", null));
        assertNull(bbInputSetupUtils.getAAIServiceSubscription(null, ""));
    }

    @Test
    public void getAAIServiceInstanceByIdTest() {
        final String serviceInstanceId = "serviceInstanceId";
        ServiceInstance expectedServiceInstance = new ServiceInstance();

        doReturn(Optional.of(expectedServiceInstance)).when(MOCK_aaiResourcesClient).get(isA(Class.class),
                isA(AAIResourceUri.class));

        assertThat(bbInputSetupUtils.getAAIServiceInstanceById(serviceInstanceId), sameBeanAs(expectedServiceInstance));
    }

    @Test
    public void getAAIServiceInstanceById_ifEmptyReturnNull() {
        doReturn(Optional.empty()).when(MOCK_aaiResourcesClient).get(eq(ServiceInstance.class),
                any(AAIResourceUri.class));

        assertNull(bbInputSetupUtils.getAAIServiceInstanceById("any"));
    }

    @Test
    public void getAAIServiceInstanceByIdAndCustomerTest() {
        final String globalCustomerId = "globalCustomerId";
        final String serviceType = "serviceType";
        final String serviceInstanceId = "serviceInstanceId";
        ServiceInstance expected = new ServiceInstance();
        expected.setServiceInstanceId(serviceInstanceId);

        doReturn(Optional.of(expected)).when(MOCK_aaiResourcesClient).get(ServiceInstance.class, AAIUriFactory
                .createResourceUri(AAIObjectType.SERVICE_INSTANCE, globalCustomerId, serviceType, serviceInstanceId)
                .depth(Depth.TWO));

        assertThat(bbInputSetupUtils.getAAIServiceInstanceByIdAndCustomer(globalCustomerId, serviceType,
                serviceInstanceId), sameBeanAs(expected));
    }

    @Test
    public void getAAIServiceInstanceByIdAndCustomerNullTest() {
        doReturn(Optional.empty()).when(MOCK_aaiResourcesClient).get(eq(ServiceInstance.class),
                any(AAIResourceUri.class));

        assertNull(bbInputSetupUtils.getAAIServiceInstanceByIdAndCustomer("", "", ""));
    }

    @Test
    public void getAAIServiceInstanceByNameTest() throws Exception {
        final String serviceInstanceName = "serviceInstanceName";

        ServiceInstance expectedServiceInstance = new ServiceInstance();
        expectedServiceInstance.setServiceInstanceId("serviceInstanceId");

        ServiceSubscription serviceSubscription = new ServiceSubscription();
        serviceSubscription.setServiceType("serviceType");

        Customer customer = new Customer();
        customer.setGlobalCustomerId("globalCustomerId");
        customer.setServiceSubscription(serviceSubscription);

        ServiceInstances serviceInstances = new ServiceInstances();
        serviceInstances.getServiceInstance().add(expectedServiceInstance);

        doReturn(Optional.of(serviceInstances)).when(MOCK_aaiResourcesClient).get(isA(Class.class),
                isA(AAIResourceUri.class));

        assertThat(bbInputSetupUtils.getAAIServiceInstanceByName(serviceInstanceName, customer),
                sameBeanAs(serviceInstances.getServiceInstance().get(0)));
    }

    @Test
    public void getAAIServiceInstanceByNameExceptionTest() throws Exception {
        final String serviceInstanceName = "serviceInstanceName";

        expectedException.expect(Exception.class);
        expectedException.expectMessage("Multiple Service Instances Returned");

        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setServiceInstanceId("serviceInstanceId");

        ServiceSubscription serviceSubscription = new ServiceSubscription();
        serviceSubscription.setServiceType("serviceType");

        Customer customer = new Customer();
        customer.setGlobalCustomerId("globalCustomerId");
        customer.setServiceSubscription(serviceSubscription);

        ServiceInstances serviceInstances = new ServiceInstances();
        serviceInstances.getServiceInstance().add(serviceInstance);
        serviceInstances.getServiceInstance().add(serviceInstance);

        doReturn(Optional.of(serviceInstances)).when(MOCK_aaiResourcesClient).get(isA(Class.class),
                isA(AAIResourceUri.class));

        bbInputSetupUtils.getAAIServiceInstanceByName(serviceInstanceName, customer);
    }

    @Test
    public void getAAIServiceInstanceByNameNullTest() throws Exception {
        Customer customer = new Customer();
        customer.setServiceSubscription(new ServiceSubscription());

        assertNull(bbInputSetupUtils.getAAIServiceInstanceByName("", customer));
    }

    @Test
    public void getOptionalAAIServiceInstanceByNameExceptionTest() throws Exception {
        expectedException.expect(MultipleObjectsFoundException.class);
        expectedException.expectMessage(containsString(
                "Multiple service instances found for customer-id: globalCustomerId, service-type: serviceType and service-instance-name: serviceInstanceId."));

        final String globalCustomerId = "globalCustomerId";
        final String serviceType = "serviceType";
        final String serviceInstanceId = "serviceInstanceId";

        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setServiceInstanceId(serviceInstanceId);
        serviceInstance.setServiceType(serviceType);

        ServiceInstances serviceInstances = new ServiceInstances();
        serviceInstances.getServiceInstance().add(serviceInstance);
        serviceInstances.getServiceInstance().add(serviceInstance);

        doReturn(Optional.of(serviceInstances)).when(MOCK_aaiResourcesClient).get(isA(Class.class),
                isA(AAIResourceUri.class));

        bbInputSetupUtils.getAAIServiceInstanceByName(globalCustomerId, serviceType, serviceInstanceId);
    }

    @Test
    public void getOptionalAAIServiceInstanceByNameNullTest() throws Exception {
        Optional<ServiceInstance> actual = bbInputSetupUtils.getAAIServiceInstanceByName("", "", "");

        assertThat(actual, sameBeanAs(Optional.empty()));
    }

    @Test
    public void getCatalogInstanceGroupNullTest() {
        assertNull(bbInputSetupUtils.getCatalogInstanceGroup(""));
    }

    @Test
    public void getCatalogInstanceGroupTest() throws IOException {
        final String modelUUID = "modelUUIDTest";
        org.onap.so.db.catalog.beans.InstanceGroup expectedInstanceGroup = mapper.readValue(
                new File(RESOURCE_PATH + "InstanceGroup.json"), org.onap.so.db.catalog.beans.InstanceGroup.class);

        doReturn(expectedInstanceGroup).when(MOCK_catalogDbClient).getInstanceGroupByModelUUID(modelUUID);

        assertThat(bbInputSetupUtils.getCatalogInstanceGroup(modelUUID), sameBeanAs(expectedInstanceGroup));
    }

    @Test
    public void getCollectionResourceInstanceGroupCustomizationTest() {
        final String modelCustomizationUUID = "modelCustomizationUUID";
        CollectionResourceInstanceGroupCustomization expectedCollection =
                new CollectionResourceInstanceGroupCustomization();

        doReturn(Arrays.asList(expectedCollection)).when(MOCK_catalogDbClient)
                .getCollectionResourceInstanceGroupCustomizationByModelCustUUID(modelCustomizationUUID);

        assertThat(bbInputSetupUtils.getCollectionResourceInstanceGroupCustomization(modelCustomizationUUID),
                sameBeanAs(Arrays.asList(expectedCollection)));
    }

    @Test
    public void getAAIConfigurationNullTest() {
        assertNull(bbInputSetupUtils.getAAIConfiguration(""));
    }

    @Test
    public void getAAIConfigurationTest() throws IOException {
        final String configurationId = "configurationId";
        Configuration expectedAaiConfiguration =
                mapper.readValue(new File(RESOURCE_PATH + "ConfigurationInput.json"), Configuration.class);

        doReturn(Optional.of(expectedAaiConfiguration)).when(MOCK_aaiResourcesClient).get(isA(Class.class),
                isA(AAIResourceUri.class));

        assertThat(bbInputSetupUtils.getAAIConfiguration(configurationId), sameBeanAs(expectedAaiConfiguration));
    }

    @Test
    public void getAAIGenericVnfNullTest() {
        assertNull(bbInputSetupUtils.getAAIGenericVnf(""));
    }

    @Test
    public void getAAIGenericVnfTest() throws IOException {
        final String vnfId = "vnfId";
        GenericVnf expectedAaiVnf =
                mapper.readValue(new File(RESOURCE_PATH + "aaiGenericVnfInput.json"), GenericVnf.class);

        doReturn(Optional.of(expectedAaiVnf)).when(MOCK_aaiResourcesClient).get(isA(Class.class),
                eq(AAIUriFactory.createResourceUri(AAIObjectType.GENERIC_VNF, vnfId).depth(Depth.ONE)));

        assertThat(bbInputSetupUtils.getAAIGenericVnf(vnfId), sameBeanAs(expectedAaiVnf));
    }

    @Test
    public void getAAIResourceDepthOneTest() {
        AAIResourceUri aaiResourceUri = AAIUriFactory.createResourceUri(AAIObjectType.GENERIC_VNF, "anyVnfId");
        AAIResourceUri expectedUri = aaiResourceUri.clone().depth(Depth.ONE);
        AAIResourceUri aaiResourceUriClone = aaiResourceUri.clone();

        bbInputSetupUtils.getAAIResourceDepthOne(aaiResourceUri);

        verify(MOCK_aaiResourcesClient, times(1)).get(expectedUri);
        assertEquals("Uri should not have changed", aaiResourceUriClone.build(), aaiResourceUri.build());
    }

    @Test
    public void getAAIResourceDepthTwoTest() {
        AAIResourceUri aaiResourceUri = AAIUriFactory.createResourceUri(AAIObjectType.GENERIC_VNF, "anyVnfId");
        AAIResourceUri expectedUri = aaiResourceUri.clone().depth(Depth.TWO);
        AAIResourceUri aaiResourceUriClone = aaiResourceUri.clone();

        bbInputSetupUtils.getAAIResourceDepthTwo(aaiResourceUri);

        verify(MOCK_aaiResourcesClient, times(1)).get(expectedUri);
        assertEquals("Uri should not have changed", aaiResourceUriClone.build(), aaiResourceUri.build());
    }

    @Test
    public void getRelatedNetworkByNameFromServiceInstanceTest() throws Exception {
        final String networkId = "id123";
        final String networkName = "name123";

        Optional<L3Networks> expected = Optional.of(new L3Networks());
        L3Network network = new L3Network();
        network.setNetworkId(networkId);
        network.setNetworkName(networkName);
        expected.get().getL3Network().add(network);

        doReturn(expected).when(MOCK_aaiResourcesClient).get(eq(L3Networks.class), any(AAIResourceUri.class));
        Optional<L3Network> actual =
                bbInputSetupUtils.getRelatedNetworkByNameFromServiceInstance(networkId, networkName);

        assertTrue(actual.isPresent());
        assertEquals(networkId, actual.get().getNetworkId());
        assertEquals(networkName, actual.get().getNetworkName());
        assertEquals(expected.get().getL3Network().get(0).getNetworkId(), actual.get().getNetworkId());
    }

    @Test
    public void getRelatedNetworkByNameFromServiceInstanceMultipleNetworksExceptionTest() throws Exception {
        final String serviceInstanceId = "serviceInstanceId";
        final String networkName = "networkName";
        expectedException.expect(MultipleObjectsFoundException.class);
        expectedException.expectMessage(
                String.format("Multiple networks found for service-instance-id: %s and network-name: %s.",
                        serviceInstanceId, networkName));

        L3Network network = new L3Network();
        network.setNetworkId("id123");
        network.setNetworkName("name123");

        L3Networks l3Networks = new L3Networks();
        l3Networks.getL3Network().add(network);
        l3Networks.getL3Network().add(network);
        Optional<L3Networks> optNetworks = Optional.of(l3Networks);

        doReturn(optNetworks).when(MOCK_aaiResourcesClient).get(eq(L3Networks.class), any(AAIResourceUri.class));

        bbInputSetupUtils.getRelatedNetworkByNameFromServiceInstance(serviceInstanceId, networkName);
    }

    @Test
    public void getRelatedNetworkByNameFromServiceInstanceNotFoundTest() throws Exception {
        assertEquals(Optional.empty(), bbInputSetupUtils.getRelatedNetworkByNameFromServiceInstance("", ""));
    }

    @Test
    public void getRelatedServiceInstanceFromInstanceGroupTest() throws Exception {
        Optional<ServiceInstances> expected = Optional.of(new ServiceInstances());
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setServiceInstanceId("serviceInstanceId");
        serviceInstance.setServiceInstanceName("serviceInstanceName");
        expected.get().getServiceInstance().add(serviceInstance);

        doReturn(expected).when(MOCK_aaiResourcesClient).get(eq(ServiceInstances.class), any(AAIResourceUri.class));
        Optional<ServiceInstance> actual = this.bbInputSetupUtils.getRelatedServiceInstanceFromInstanceGroup("ig-001");

        assertTrue(actual.isPresent());
        assertEquals(expected.get().getServiceInstance().get(0).getServiceInstanceId(),
                actual.get().getServiceInstanceId());
    }

    @Test
    public void getRelatedServiceInstanceFromInstanceGroupMultipleExceptionTest() throws Exception {
        final String instanceGroupId = "ig-001";
        expectedException.expect(MultipleObjectsFoundException.class);
        expectedException.expectMessage(
                String.format("Mulitple service instances were found for instance-group-id: %s.", instanceGroupId));

        Optional<ServiceInstances> serviceInstances = Optional.of(new ServiceInstances());
        ServiceInstance si1 = Mockito.mock(ServiceInstance.class);
        ServiceInstance si2 = Mockito.mock(ServiceInstance.class);
        serviceInstances.get().getServiceInstance().add(si1);
        serviceInstances.get().getServiceInstance().add(si2);

        doReturn(serviceInstances).when(MOCK_aaiResourcesClient).get(eq(ServiceInstances.class),
                any(AAIResourceUri.class));
        bbInputSetupUtils.getRelatedServiceInstanceFromInstanceGroup(instanceGroupId);
    }

    @Test
    public void getRelatedServiceInstanceFromInstanceGroupNotFoundExceptionTest() throws Exception {
        expectedException.expect(NoServiceInstanceFoundException.class);
        expectedException.expectMessage("No ServiceInstances Returned");

        Optional<ServiceInstances> serviceInstances = Optional.of(new ServiceInstances());

        doReturn(serviceInstances).when(MOCK_aaiResourcesClient).get(eq(ServiceInstances.class),
                any(AAIResourceUri.class));
        bbInputSetupUtils.getRelatedServiceInstanceFromInstanceGroup("ig-001");
    }

    @Test
    public void getRelatedVnfByNameFromServiceInstanceTest() throws Exception {
        final String vnfId = "id123";
        final String vnfName = "name123";

        Optional<GenericVnfs> expected = Optional.of(new GenericVnfs());
        GenericVnf vnf = new GenericVnf();
        vnf.setVnfId(vnfId);
        vnf.setVnfName(vnfName);
        expected.get().getGenericVnf().add(vnf);

        doReturn(expected).when(MOCK_aaiResourcesClient).get(eq(GenericVnfs.class), any(AAIResourceUri.class));
        Optional<GenericVnf> actual = this.bbInputSetupUtils.getRelatedVnfByNameFromServiceInstance(vnfId, vnfName);

        assertTrue(actual.isPresent());
        assertEquals(expected.get().getGenericVnf().get(0).getVnfId(), actual.get().getVnfId());
    }

    @Test
    public void getRelatedVnfByNameFromServiceInstanceMultipleVnfsExceptionTest() throws Exception {
        final String serviceInstanceId = "serviceInstanceId";
        final String vnfName = "vnfName";
        expectedException.expect(MultipleObjectsFoundException.class);
        expectedException.expectMessage(String.format(
                "Multiple vnfs found for service-instance-id: %s and vnf-name: %s.", serviceInstanceId, vnfName));

        GenericVnf vnf = new GenericVnf();
        vnf.setVnfId("id123");
        vnf.setVnfName("name123");

        GenericVnfs vnfs = new GenericVnfs();
        vnfs.getGenericVnf().add(vnf);
        vnfs.getGenericVnf().add(vnf);

        Optional<GenericVnfs> optVnfs = Optional.of(vnfs);
        doReturn(optVnfs).when(MOCK_aaiResourcesClient).get(eq(GenericVnfs.class), any(AAIResourceUri.class));

        bbInputSetupUtils.getRelatedVnfByNameFromServiceInstance(serviceInstanceId, vnfName);
    }

    @Test
    public void getRelatedVnfByNameFromServiceInstanceNotFoundTest() throws Exception {
        final String serviceInstanceId = "serviceInstanceId";
        final String vnfName = "vnfName";

        assertEquals(Optional.empty(),
                bbInputSetupUtils.getRelatedVnfByNameFromServiceInstance(serviceInstanceId, vnfName));
    }

    @Test
    public void getRelatedVolumeGroupByNameFromVnfTest() throws Exception {
        final String vnfId = "id123";
        final String vnfName = "name123";

        Optional<VolumeGroups> expected = Optional.of(new VolumeGroups());
        VolumeGroup volumeGroup = new VolumeGroup();
        volumeGroup.setVolumeGroupId(vnfId);
        volumeGroup.setVolumeGroupName(vnfName);
        expected.get().getVolumeGroup().add(volumeGroup);

        doReturn(expected).when(MOCK_aaiResourcesClient).get(eq(VolumeGroups.class), any(AAIResourceUri.class));
        Optional<VolumeGroup> actual = this.bbInputSetupUtils.getRelatedVolumeGroupByNameFromVnf(vnfId, vnfName);

        assertTrue(actual.isPresent());
        assertEquals(expected.get().getVolumeGroup().get(0).getVolumeGroupId(), actual.get().getVolumeGroupId());
    }

    @Test
    public void getRelatedVolumeGroupByNameFromVnfMultipleVolumeGroupsExceptionTest() throws Exception {
        final String vnfId = "vnfId";
        final String volumeGroupName = "volumeGroupName";
        expectedException.expect(MultipleObjectsFoundException.class);
        expectedException.expectMessage(String.format(
                "Multiple volume-groups found for vnf-id: %s and volume-group-name: %s.", vnfId, volumeGroupName));

        VolumeGroup volumeGroup = new VolumeGroup();
        volumeGroup.setVolumeGroupId("id123");
        volumeGroup.setVolumeGroupName("name123");

        VolumeGroups volumeGroups = new VolumeGroups();
        volumeGroups.getVolumeGroup().add(volumeGroup);
        volumeGroups.getVolumeGroup().add(volumeGroup);
        Optional<VolumeGroups> optVolumeGroups = Optional.of(volumeGroups);

        doReturn(optVolumeGroups).when(MOCK_aaiResourcesClient).get(eq(VolumeGroups.class), any(AAIResourceUri.class));

        bbInputSetupUtils.getRelatedVolumeGroupByNameFromVnf(vnfId, volumeGroupName);
    }

    @Test
    public void getRelatedVolumeGroupByNameFromVnfNotFoundTest() throws Exception {
        assertEquals(Optional.empty(), bbInputSetupUtils.getRelatedVolumeGroupByNameFromVnf("", ""));
    }

    @Test
    public void getRelatedVolumeGroupByNameFromVfModuleTest() throws Exception {
        Optional<VolumeGroups> expected = Optional.of(new VolumeGroups());
        VolumeGroup volumeGroup = new VolumeGroup();
        volumeGroup.setVolumeGroupId("id123");
        volumeGroup.setVolumeGroupName("name123");
        expected.get().getVolumeGroup().add(volumeGroup);

        doReturn(expected).when(MOCK_aaiResourcesClient).get(eq(VolumeGroups.class), any(AAIResourceUri.class));
        Optional<VolumeGroup> actual =
                this.bbInputSetupUtils.getRelatedVolumeGroupByNameFromVfModule("id123", "id123", "name123");

        assertTrue(actual.isPresent());
        assertEquals(expected.get().getVolumeGroup().get(0).getVolumeGroupId(), actual.get().getVolumeGroupId());
    }

    @Test
    public void getRelatedVolumeGroupFromVfModuleMultipleVolumeGroupsExceptionTest() throws Exception {
        expectedException.expect(Exception.class);
        final String vnfId = "vnfId";
        final String volumeGroupId = "volumeGroupId";

        VolumeGroup volumeGroup = new VolumeGroup();
        volumeGroup.setVolumeGroupId("id123");
        volumeGroup.setVolumeGroupName("name123");

        VolumeGroups expectedVolumeGroup = new VolumeGroups();
        expectedVolumeGroup.getVolumeGroup().add(volumeGroup);
        expectedVolumeGroup.getVolumeGroup().add(volumeGroup);

        doReturn(expectedVolumeGroup).when(MOCK_aaiResourcesClient).get(eq(VolumeGroups.class),
                any(AAIResourceUri.class));

        bbInputSetupUtils.getRelatedVolumeGroupFromVfModule(vnfId, volumeGroupId);
    }

    @Test
    public void getRelatedVolumeGroupFromVfModuleNotFoundTest() throws Exception {
        final String vnfId = "vnfId";
        final String volumeGroupId = "volumeGroupId";

        assertEquals(Optional.empty(), bbInputSetupUtils.getRelatedVolumeGroupFromVfModule(vnfId, volumeGroupId));
    }

    @Test
    public void getRelatedVolumeGroupFromVfModuleTest() throws Exception {
        Optional<VolumeGroups> expected = Optional.of(new VolumeGroups());
        VolumeGroup volumeGroup = new VolumeGroup();
        volumeGroup.setVolumeGroupId("id123");
        expected.get().getVolumeGroup().add(volumeGroup);

        doReturn(expected).when(MOCK_aaiResourcesClient).get(eq(VolumeGroups.class), any(AAIResourceUri.class));
        Optional<VolumeGroup> actual = bbInputSetupUtils.getRelatedVolumeGroupFromVfModule("id123", "id123");

        assertTrue(actual.isPresent());
        assertEquals(expected.get().getVolumeGroup().get(0).getVolumeGroupId(), actual.get().getVolumeGroupId());
    }

    @Test
    public void getRelatedVolumeGroupByNameFromVfModuleMultipleVolumeGroupsExceptionTest() throws Exception {
        final String vnfId = "vnfId";
        final String vfModuleId = "vfModuleId";
        final String volumeGroupName = "volumeGroupName";

        expectedException.expect(MultipleObjectsFoundException.class);
        expectedException.expectMessage(String.format(
                "Multiple voulme-groups found for vnf-id: %s, vf-module-id: %s and volume-group-name: %s.", vnfId,
                vfModuleId, volumeGroupName));

        VolumeGroup volumeGroup = new VolumeGroup();
        volumeGroup.setVolumeGroupId("id123");
        volumeGroup.setVolumeGroupName("name123");

        VolumeGroups volumeGroups = new VolumeGroups();
        volumeGroups.getVolumeGroup().add(volumeGroup);
        volumeGroups.getVolumeGroup().add(volumeGroup);

        Optional<VolumeGroups> optVolumeGroups = Optional.of(volumeGroups);
        doReturn(optVolumeGroups).when(MOCK_aaiResourcesClient).get(eq(VolumeGroups.class), any(AAIResourceUri.class));

        bbInputSetupUtils.getRelatedVolumeGroupByNameFromVfModule(vnfId, vfModuleId, volumeGroupName);
    }

    @Test
    public void getRelatedVolumeGroupByNameFromVfModuleNotFoundTest() throws Exception {
        assertEquals(Optional.empty(), bbInputSetupUtils.getRelatedVolumeGroupByNameFromVfModule("", "", ""));
    }

    @Test
    public void loadOriginalFlowExecutionPathTest() throws IOException {
        final String requestId = "123";
        final String originalRequestId = "originalRequestId";
        final String flowsToExecuteString = new String(
                Files.readAllBytes(Paths.get(RESOURCE_PATH + "FlowsToExecute.json")), StandardCharsets.UTF_8);

        InfraActiveRequests request = new InfraActiveRequests();
        ExecuteBuildingBlock[] expectedFlowsToExecute =
                mapper.readValue(flowsToExecuteString, ExecuteBuildingBlock[].class);

        request.setRequestId(requestId);
        request.setOriginalRequestId(originalRequestId);
        doReturn(request).when(MOCK_requestsDbClient).getInfraActiveRequestbyRequestId(requestId);

        RequestProcessingData requestProcessingData = new RequestProcessingData();
        requestProcessingData.setValue(flowsToExecuteString);
        doReturn(requestProcessingData).when(MOCK_requestsDbClient)
                .getRequestProcessingDataBySoRequestIdAndName(anyString(), anyString());

        List<ExecuteBuildingBlock> flowsToExecute = bbInputSetupUtils.loadOriginalFlowExecutionPath(requestId);

        assertEquals(mapper.writeValueAsString(expectedFlowsToExecute), mapper.writeValueAsString(flowsToExecute));
    }

    @Test
    public void getRelatedConfigurationByNameFromServiceInstanceExceptionTest() throws Exception {
        Configuration configuration = new Configuration();
        configuration.setConfigurationId("id123");

        Configurations configurations = new Configurations();
        configurations.getConfiguration().add(configuration);
        configurations.getConfiguration().add(configuration);

        Optional<Configurations> optConfigurations = Optional.of(configurations);

        doReturn(optConfigurations).when(MOCK_aaiResourcesClient).get(eq(Configurations.class),
                any(AAIResourceUri.class));

        expectedException.expect(MultipleObjectsFoundException.class);
        this.bbInputSetupUtils.getRelatedConfigurationByNameFromServiceInstance("id123", "name123");
    }

    @Test
    public void getRelatedConfigurationByNameFromServiceInstanceNotFoundTest() throws Exception {
        assertEquals(Optional.empty(), bbInputSetupUtils.getRelatedConfigurationByNameFromServiceInstance("", ""));
    }

    @Test
    public void getRelatedConfigurationByNameFromServiceInstanceTest() throws Exception {
        Optional<Configurations> expected = Optional.of(new Configurations());
        Configuration configuration = new Configuration();
        configuration.setConfigurationId("id123");
        expected.get().getConfiguration().add(configuration);

        doReturn(expected).when(MOCK_aaiResourcesClient).get(eq(Configurations.class), any(AAIResourceUri.class));
        Optional<Configuration> actual =
                this.bbInputSetupUtils.getRelatedConfigurationByNameFromServiceInstance("id123", "name123");

        assertTrue(actual.isPresent());
        assertEquals(expected.get().getConfiguration().get(0).getConfigurationId(), actual.get().getConfigurationId());
    }

    @Test
    public void existsAAIVfModuleGloballyByNameTest() {
        AAIResourceUri expectedUri =
                AAIUriFactory.createNodesUri(AAIObjectPlurals.VF_MODULE).queryParam("vf-module-name", "testVfModule");
        bbInputSetupUtils.existsAAIVfModuleGloballyByName("testVfModule");

        verify(MOCK_aaiResourcesClient, times(1)).exists(expectedUri);
    }

    @Test
    public void existsAAIConfigurationGloballyByNameTest() {
        AAIResourceUri expectedUri = AAIUriFactory.createResourceUri(AAIObjectPlurals.CONFIGURATION)
                .queryParam("configuration-name", "testConfig");
        bbInputSetupUtils.existsAAIConfigurationGloballyByName("testConfig");

        verify(MOCK_aaiResourcesClient, times(1)).exists(expectedUri);
    }

    @Test
    public void existsAAINetworksGloballyByNameTest() {
        AAIResourceUri expectedUri =
                AAIUriFactory.createResourceUri(AAIObjectPlurals.L3_NETWORK).queryParam("network-name", "testNetwork");
        bbInputSetupUtils.existsAAINetworksGloballyByName("testNetwork");

        verify(MOCK_aaiResourcesClient, times(1)).exists(expectedUri);
    }

    @Test
    public void existsAAIVolumeGroupGloballyByNameTest() {
        AAIResourceUri expectedUri = AAIUriFactory.createNodesUri(AAIObjectPlurals.VOLUME_GROUP)
                .queryParam("volume-group-name", "testVoumeGroup");

        bbInputSetupUtils.existsAAIVolumeGroupGloballyByName("testVoumeGroup");
        verify(MOCK_aaiResourcesClient, times(1)).exists(expectedUri);
    }

    @Test
    public void shouldChangeInfraActiveRequestVnfId() throws IOException {
        final String vnfId = "vnfId";
        InfraActiveRequests infraActiveRequests = loadExpectedInfraActiveRequest();

        bbInputSetupUtils.updateInfraActiveRequestVnfId(infraActiveRequests, vnfId);

        assertEquals(infraActiveRequests.getVnfId(), vnfId);
    }

    @Test
    public void shouldChangeInfraActiveRequestVfModuleId() throws IOException {
        final String vfModuleId = "vfModuleId";
        InfraActiveRequests infraActiveRequests = loadExpectedInfraActiveRequest();

        bbInputSetupUtils.updateInfraActiveRequestVfModuleId(infraActiveRequests, vfModuleId);

        assertEquals(infraActiveRequests.getVfModuleId(), vfModuleId);
    }

    @Test
    public void shouldChangeInfraActiveRequestVolumeGroupId() throws IOException {
        final String volumeGroupId = "volumeGroupId";
        InfraActiveRequests infraActiveRequests = loadExpectedInfraActiveRequest();

        bbInputSetupUtils.updateInfraActiveRequestVolumeGroupId(infraActiveRequests, volumeGroupId);

        assertEquals(infraActiveRequests.getVolumeGroupId(), volumeGroupId);
    }

    @Test
    public void shouldChangeInfraActiveRequestNetworkId() throws IOException {
        final String networkId = "activeRequestNetworkId";
        InfraActiveRequests infraActiveRequests = loadExpectedInfraActiveRequest();

        bbInputSetupUtils.updateInfraActiveRequestNetworkId(infraActiveRequests, networkId);

        assertEquals(infraActiveRequests.getNetworkId(), networkId);
    }

    @Test
    public void getAAIVpnBindingNullTest() {
        assertNull(bbInputSetupUtils.getAAIVpnBinding("vpnBindingId"));
    }

    @Test
    public void getAAIVolumeGroupNullTest() {
        VolumeGroup actualAaiVnf =
                bbInputSetupUtils.getAAIVolumeGroup("cloudOwnerId", "cloudRegionId", "volumeGroupId");
        assertNull(actualAaiVnf);
    }

    @Test
    public void getAAIVfModuleNullTest() {
        assertNull(bbInputSetupUtils.getAAIVfModule("vnfId", "vfModuleId"));
    }

    @Test
    public void getAAIL3NetworkNullTest() {
        assertNull(bbInputSetupUtils.getAAIL3Network("networkId"));
    }

    @Test
    public void getAICVpnBindingFromNetwork_noVpnBindingTest() throws IOException {
        L3Network l3Network =
                mapper.readValue(new File(RESOURCE_PATH + "aaiL3NetworkInputWithSubnets.json"), L3Network.class);

        Optional<VpnBinding> actual = bbInputSetupUtils.getAICVpnBindingFromNetwork(l3Network);
        assertEquals(actual, Optional.empty());
    }

    @Test
    public void getAAIServiceInstancesGloballyByName_noAAIResourceTest() {
        final String serviceInstanceName = "serviceInstanceName";

        doReturn(Optional.empty()).when(MOCK_aaiResourcesClient).get(isA(Class.class), isA(AAIResourceUri.class));
        ServiceInstances actualServiceInstances =
                bbInputSetupUtils.getAAIServiceInstancesGloballyByName(serviceInstanceName);

        assertNull(actualServiceInstances);
    }

    @Test
    public void getAAIVnfsGloballyByName_noAAIResourceTest() {
        final String vnfName = "vnfName";

        doReturn(Optional.empty()).when(MOCK_aaiResourcesClient).get(isA(Class.class), isA(AAIResourceUri.class));
        GenericVnfs actualGenericVnfs = bbInputSetupUtils.getAAIVnfsGloballyByName(vnfName);

        assertNull(actualGenericVnfs);
    }

    private InfraActiveRequests loadExpectedInfraActiveRequest() throws IOException {
        return mapper.readValue(new File(RESOURCE_PATH + "InfraActiveRequestExpected.json"), InfraActiveRequests.class);
    }
}
