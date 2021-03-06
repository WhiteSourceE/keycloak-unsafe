package org.keycloak.example;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.HttpClientBuilder;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.util.JsonSerialization;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AdminClient {

    static class TypedList extends ArrayList<RoleRepresentation> {
    }

    public static class Failure extends Exception {
        private int status;

        public Failure(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }

    public static List<RoleRepresentation> getRealmRoles(HttpServletRequest req) throws Failure {
        KeycloakSecurityContext session = (KeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());

        HttpClient client = new HttpClientBuilder()
                .disableTrustManager().build();
        try {
            HttpGet get = new HttpGet(getBaseUrl(req) + "/auth/admin/realms/demo/roles");
            get.addHeader("Authorization", "Bearer " + session.getTokenString());
            try {
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new Failure(response.getStatusLine().getStatusCode());
                }
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                try {
                    return JsonSerialization.readValue(is, TypedList.class);
                } finally {
                    is.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    public static String getBaseUrl(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        return url.substring(0, url.indexOf('/', 8));
    }

}
