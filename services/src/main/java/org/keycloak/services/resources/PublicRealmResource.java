package org.keycloak.services.resources;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.idm.PublishedRealmRepresentation;
import org.keycloak.services.resources.admin.AdminRoot;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * Resource class for public realm information
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PublicRealmResource {
    protected static final  Logger logger = Logger.getLogger(PublicRealmResource.class);

    @Context
    protected UriInfo uriInfo;

    protected RealmModel realm;

    public PublicRealmResource(RealmModel realm) {
        this.realm = realm;
    }

    /**
     * Public information about the realm.
     *
     * @return
     */
    @GET
    @NoCache
    @Produces("application/json")
    public PublishedRealmRepresentation getRealm() {
        return realmRep(realm, uriInfo);
    }

    public static PublishedRealmRepresentation realmRep(RealmModel realm, UriInfo uriInfo) {
        PublishedRealmRepresentation rep = new PublishedRealmRepresentation();
        rep.setRealm(realm.getName());
        rep.setTokenServiceUrl(TokenService.tokenServiceBaseUrl(uriInfo).build(realm.getName()).toString());
        rep.setAccountServiceUrl(AccountService.accountServiceBaseUrl(uriInfo).build(realm.getName()).toString());
        rep.setAdminApiUrl(uriInfo.getBaseUriBuilder().path(AdminRoot.class).build().toString());
        rep.setPublicKeyPem(realm.getPublicKeyPem());
        rep.setNotBefore(realm.getNotBefore());
        return rep;
    }


}
