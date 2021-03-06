package org.keycloak.jose.jws;

import org.keycloak.jose.jws.crypto.HMACProvider;
import org.keycloak.jose.jws.crypto.RSAProvider;
import org.keycloak.util.Base64Url;
import org.keycloak.util.JsonSerialization;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JWSBuilder {
    String type;
    String contentType;
    byte[] contentBytes;

    public JWSBuilder type(String type) {
        this.type = type;
        return this;
    }

    public JWSBuilder contentType(String type) {
        this.contentType = type;
        return this;
    }

    public EncodingBuilder content(byte[] bytes) {
        this.contentBytes = bytes;
        return new EncodingBuilder();
    }

    public EncodingBuilder jsonContent(Object object) {
        try {
            this.contentBytes = JsonSerialization.writeValueAsBytes(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new EncodingBuilder();
    }


    protected String encodeHeader(Algorithm alg) {
        StringBuilder builder = new StringBuilder("{");
        builder.append("\"alg\":\"").append(alg.toString()).append("\"");

        if (type != null) builder.append(",\"typ\" : \"").append(type).append("\"");
        if (contentType != null) builder.append(",\"cty\":\"").append(contentType).append("\"");
        builder.append("}");
        try {
            return Base64Url.encode(builder.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String encode(Algorithm alg, byte[] data, byte[] signature) {
        StringBuffer encoding = new StringBuffer();
        encoding.append(encodeHeader(alg));
        encoding.append('.');
        encoding.append(Base64Url.encode(data));
        encoding.append('.');
        if (alg != Algorithm.none) {
            encoding.append(Base64Url.encode(signature));
        }
        return encoding.toString();
    }

    protected byte[] marshalContent() {
        return contentBytes;
    }

    public class EncodingBuilder {
        public String none() {
            byte[] data = marshalContent();
            return encode(Algorithm.none, data, null);
        }

        public String rsa256(PrivateKey privateKey) {
            byte[] data = marshalContent();
            byte[] signature = RSAProvider.sign(data, Algorithm.RS256, privateKey);
            return encode(Algorithm.RS256, data, signature);
        }

        public String rsa384(PrivateKey privateKey) {
            byte[] data = marshalContent();
            byte[] signature = RSAProvider.sign(data, Algorithm.RS384, privateKey);
            return encode(Algorithm.RS384, data, signature);
        }

        public String rsa512(PrivateKey privateKey) {
            byte[] data = marshalContent();
            byte[] signature = RSAProvider.sign(data, Algorithm.RS512, privateKey);
            return encode(Algorithm.RS512, data, signature);
        }


        public String hmac256(byte[] sharedSecret) {
            byte[] data = marshalContent();
            byte[] signature = HMACProvider.sign(data, Algorithm.HS256, sharedSecret);
            return encode(Algorithm.HS256, data, signature);
        }

        public String hmac384(byte[] sharedSecret) {
            byte[] data = marshalContent();
            byte[] signature = HMACProvider.sign(data, Algorithm.HS384, sharedSecret);
            return encode(Algorithm.HS384, data, signature);
        }

        public String hmac512(byte[] sharedSecret) {
            byte[] data = marshalContent();
            byte[] signature = HMACProvider.sign(data, Algorithm.HS512, sharedSecret);
            return encode(Algorithm.HS512, data, signature);
        }

        public String hmac256(SecretKey sharedSecret) {
            byte[] data = marshalContent();
            byte[] signature = HMACProvider.sign(data, Algorithm.HS256, sharedSecret);
            return encode(Algorithm.HS256, data, signature);
        }

        public String hmac384(SecretKey sharedSecret) {
            byte[] data = marshalContent();
            byte[] signature = HMACProvider.sign(data, Algorithm.HS384, sharedSecret);
            return encode(Algorithm.HS384, data, signature);
        }

        public String hmac512(SecretKey sharedSecret) {
            byte[] data = marshalContent();
            byte[] signature = HMACProvider.sign(data, Algorithm.HS512, sharedSecret);
            return encode(Algorithm.HS512, data, signature);
        }
    }
}
