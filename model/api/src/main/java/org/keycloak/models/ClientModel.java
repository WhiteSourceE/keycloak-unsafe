package org.keycloak.models;

import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ClientModel {
    /**
     * Internal database key
     *
     * @return
     */
    String getId();

    /**
     * String exposed to outside world
     *
     * @return
     */
    String getClientId();

    long getAllowedClaimsMask();

    void setAllowedClaimsMask(long mask);

    Set<String> getWebOrigins();

    void setWebOrigins(Set<String> webOrigins);

    void addWebOrigin(String webOrigin);

    void removeWebOrigin(String webOrigin);

    Set<String> getRedirectUris();

    void setRedirectUris(Set<String> redirectUris);

    void addRedirectUri(String redirectUri);

    void removeRedirectUri(String redirectUri);


    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean validateSecret(String secret);
    String getSecret();
    public void setSecret(String secret);

    boolean isFullScopeAllowed();
    void setFullScopeAllowed(boolean value);

    boolean isPublicClient();
    void setPublicClient(boolean flag);

    boolean isDirectGrantsOnly();
    void setDirectGrantsOnly(boolean flag);

    Set<RoleModel> getScopeMappings();
    void addScopeMapping(RoleModel role);
    void deleteScopeMapping(RoleModel role);
    Set<RoleModel> getRealmScopeMappings();
    boolean hasScope(RoleModel role);


    RealmModel getRealm();

    /**
     * Time in seconds since epoc
     *
     * @return
     */
    int getNotBefore();

    void setNotBefore(int notBefore);

}
