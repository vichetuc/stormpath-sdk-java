/*
 * Copyright 2013 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.group;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupMembership;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.lang.Assert;

import java.util.Map;

/**
 * @since 0.4
 */
public class DefaultGroupMembership extends AbstractInstanceResource implements GroupMembership {

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Account> ACCOUNT = new ResourceReference<Account>("account", Account.class);
    static final ResourceReference<Group> GROUP = new ResourceReference<Group>("group", Group.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(ACCOUNT, GROUP);

    public DefaultGroupMembership(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultGroupMembership(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public Account getAccount() {
        return getResourceProperty(ACCOUNT);
    }

    @Override
    public Group getGroup() {
        return getResourceProperty(GROUP);
    }

    /**
     * THIS IS NOT PART OF THE STORMPATH PUBLIC API.  SDK end-users should not call it - it could be removed or
     * changed at any time.  It has the public modifier only as an implementation technique to be accessible to other
     * {@code Default*} implementations.
     *
     * @param account   the account to associate with the group.
     * @param group     the group which will contain the account.
     * @param dataStore the datastore used to create the membership
     * @return the created GroupMembership instance.
     */
    public static GroupMembership create(Account account, Group group, InternalDataStore dataStore) {

        Assert.hasText(account.getHref(), "Account does not yet have an 'href'.  You must first persist the account " +
                "before assigning it to a Group.");
        Assert.hasText(group.getHref(), "Group does not yet have an 'href'.  You must first persist the group " +
                "before assigning accounts to it.");

        GroupMembership groupMembership = dataStore.instantiate(GroupMembership.class);

        Assert.isInstanceOf(DefaultGroupMembership.class, groupMembership, "GroupMembership instance is not an expected " +
                DefaultGroupMembership.class.getName() + " instance.");

        DefaultGroupMembership dgm = (DefaultGroupMembership)groupMembership;

        dgm.setResourceProperty(GROUP, group);
        dgm.setResourceProperty(ACCOUNT, account);

        //TODO: enable auto discovery
        String href = "/groupMemberships";

        return dataStore.create(href, groupMembership);
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }
}
