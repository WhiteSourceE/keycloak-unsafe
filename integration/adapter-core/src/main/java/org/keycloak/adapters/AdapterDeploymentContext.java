package org.keycloak.adapters;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.jboss.logging.Logger;
import org.keycloak.enums.SslRequired;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.keycloak.representations.idm.PublishedRealmRepresentation;
import org.keycloak.util.JsonSerialization;
import org.keycloak.util.KeycloakUriBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.PublicKey;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AdapterDeploymentContext {
    private static final Logger log = Logger.getLogger(AdapterDeploymentContext.class);
    protected KeycloakDeployment deployment;

    public AdapterDeploymentContext() {
    }

    public AdapterDeploymentContext(KeycloakDeployment deployment) {
        this.deployment = deployment;
    }

    public KeycloakDeployment getDeployment() {
        return deployment;
    }

    /**
     * Resolve adapter deployment based on partial adapter configuration.
     * This will resolve a relative auth server url based on the current request
     * This will lazily resolve the public key of the realm if it is not set already.
     *
     * @return
     */
    public KeycloakDeployment resolveDeployment(HttpFacade facade) {
        KeycloakDeployment deployment = this.deployment;
        if (deployment == null) return null;
        if (deployment.getAuthServerBaseUrl() == null) return deployment;
        if (deployment.relativeUrls) {
            deployment = new DeploymentDelegate(this.deployment);
            deployment.setAuthServerBaseUrl(getBaseBuilder(facade, this.deployment.getAuthServerBaseUrl()).build().toString());
        }
        if (deployment.getRealmKey() == null) resolveRealmKey(deployment);
        return deployment;
    }

    protected void resolveRealmKey(KeycloakDeployment deployment) {
        if (deployment.getClient() == null) {
            throw new RuntimeException("KeycloakDeployment was never initialized through appropriate SPIs");
        }
        HttpGet get = new HttpGet(deployment.getRealmInfoUrl());
        try {
            HttpResponse response = deployment.getClient().execute(get);
            int status = response.getStatusLine().getStatusCode();
            if (status != 200) {
                close(response);
                throw new RuntimeException("Unable to resolve realm public key remotely, status = " + status);
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new RuntimeException("Unable to resolve realm public key remotely.  There was no entity.");
            }
            InputStream is = entity.getContent();
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                int c;
                while ((c = is.read()) != -1) {
                    os.write(c);
                }
                byte[] bytes = os.toByteArray();
                String json = new String(bytes);
                PublishedRealmRepresentation rep = JsonSerialization.readValue(json, PublishedRealmRepresentation.class);
                deployment.setRealmKey(rep.getPublicKey());
            } finally {
                try {
                    is.close();
                } catch (IOException ignored) {

                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to resolve realm public key remotely", e);
        }
    }

    /**
     * This delegate is used to store temporary, per-request metadata like request resolved URLs.
     * Ever method is delegated except URL get methods and isConfigured()
     *
     */
    protected static class DeploymentDelegate extends KeycloakDeployment {
        protected KeycloakDeployment delegate;

        public DeploymentDelegate(KeycloakDeployment delegate) {
            this.delegate = delegate;
        }

        @Override
        public String getResourceName() {
            return delegate.getResourceName();
        }

        @Override
        public String getRealm() {
            return delegate.getRealm();
        }

        @Override
        public void setRealm(String realm) {
            delegate.setRealm(realm);
        }

        @Override
        public PublicKey getRealmKey() {
            return delegate.getRealmKey();
        }

        @Override
        public void setRealmKey(PublicKey realmKey) {
            delegate.setRealmKey(realmKey);
        }

        @Override
        public void setResourceName(String resourceName) {
            delegate.setResourceName(resourceName);
        }

        @Override
        public boolean isBearerOnly() {
            return delegate.isBearerOnly();
        }

        @Override
        public void setBearerOnly(boolean bearerOnly) {
            delegate.setBearerOnly(bearerOnly);
        }

        @Override
        public boolean isPublicClient() {
            return delegate.isPublicClient();
        }

        @Override
        public void setPublicClient(boolean publicClient) {
            delegate.setPublicClient(publicClient);
        }

        @Override
        public Map<String, String> getResourceCredentials() {
            return delegate.getResourceCredentials();
        }

        @Override
        public void setResourceCredentials(Map<String, String> resourceCredentials) {
            delegate.setResourceCredentials(resourceCredentials);
        }

        @Override
        public HttpClient getClient() {
            return delegate.getClient();
        }

        @Override
        public void setClient(HttpClient client) {
            delegate.setClient(client);
        }

        @Override
        public String getScope() {
            return delegate.getScope();
        }

        @Override
        public void setScope(String scope) {
            delegate.setScope(scope);
        }

        @Override
        public SslRequired getSslRequired() {
            return delegate.getSslRequired();
        }

        @Override
        public void setSslRequired(SslRequired sslRequired) {
            delegate.setSslRequired(sslRequired);
        }

        @Override
        public String getStateCookieName() {
            return delegate.getStateCookieName();
        }

        @Override
        public void setStateCookieName(String stateCookieName) {
            delegate.setStateCookieName(stateCookieName);
        }

        @Override
        public boolean isUseResourceRoleMappings() {
            return delegate.isUseResourceRoleMappings();
        }

        @Override
        public void setUseResourceRoleMappings(boolean useResourceRoleMappings) {
            delegate.setUseResourceRoleMappings(useResourceRoleMappings);
        }

        @Override
        public boolean isCors() {
            return delegate.isCors();
        }

        @Override
        public void setCors(boolean cors) {
            delegate.setCors(cors);
        }

        @Override
        public int getCorsMaxAge() {
            return delegate.getCorsMaxAge();
        }

        @Override
        public void setCorsMaxAge(int corsMaxAge) {
            delegate.setCorsMaxAge(corsMaxAge);
        }

        @Override
        public String getCorsAllowedHeaders() {
            return delegate.getCorsAllowedHeaders();
        }

        @Override
        public void setNotBefore(int notBefore) {
            delegate.setNotBefore(notBefore);
        }

        @Override
        public int getNotBefore() {
            return delegate.getNotBefore();
        }

        @Override
        public void setExposeToken(boolean exposeToken) {
            delegate.setExposeToken(exposeToken);
        }

        @Override
        public boolean isExposeToken() {
            return delegate.isExposeToken();
        }

        @Override
        public void setCorsAllowedMethods(String corsAllowedMethods) {
            delegate.setCorsAllowedMethods(corsAllowedMethods);
        }

        @Override
        public String getCorsAllowedMethods() {
            return delegate.getCorsAllowedMethods();
        }

        @Override
        public void setCorsAllowedHeaders(String corsAllowedHeaders) {
            delegate.setCorsAllowedHeaders(corsAllowedHeaders);
        }
    }

    protected KeycloakUriBuilder getBaseBuilder(HttpFacade facade, String base) {
        KeycloakUriBuilder builder = KeycloakUriBuilder.fromUri(base);
        URI request = URI.create(facade.getRequest().getURI());
        String scheme = request.getScheme();
        if (deployment.getSslRequired().isRequired(facade.getRequest().getRemoteAddr())) {
            scheme = "https";
            if (!request.getScheme().equals(scheme) && request.getPort() != -1) {
                log.error("request scheme: " + request.getScheme() + " ssl required");
                throw new RuntimeException("Can't resolve relative url from adapter config.");
            }
        }
        builder.scheme(scheme);
        builder.host(request.getHost());
        if (request.getPort() != -1) {
           builder.port(request.getPort());
        }
        return builder;
    }



    protected void close(HttpResponse response) {
        if (response.getEntity() != null) {
            try {
                response.getEntity().getContent().close();
            } catch (IOException e) {

            }
        }
    }

    public void updateDeployment(AdapterConfig config) {
        deployment = KeycloakDeploymentBuilder.build(config);
    }
}
