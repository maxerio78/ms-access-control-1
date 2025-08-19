package sunseries.travel.access.control.configuration;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.convert.CouchbaseCustomConversions;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;

import com.couchbase.client.core.env.QueryServiceConfig;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;

@Configuration
@EnableCouchbaseRepositories
public class CouchbaseConfiguration extends AbstractCouchbaseConfiguration {
    @Value("${couchbase.bucket.name}")
    private String bucketName;

    @Value("${couchbase.bucket.username}")
    private String bucketUserName;

    @Value("${couchbase.bucket.password}")
    private String bucketPassword;

    @Value("#{'${couchbase.host}'.split(',')}")
    private List<String> couchbaseNodes;

    @Value("${couchbase.env.timeouts.connect}")
    private long connectTimeout;

    @Value("${couchbase.env.timeouts.socket-connect}")
    private int socketTimeout;

    @Override
    protected String getBucketName() {
        return bucketName;
    }

    @Override
    protected String getUsername() {
        return bucketUserName;
    }

    @Override
    protected String getBucketPassword() {
        return bucketPassword;
    }

    @Override
    protected List<String> getBootstrapHosts() {
        return couchbaseNodes;
    }

    @Override
    protected CouchbaseEnvironment getEnvironment() {
        DefaultCouchbaseEnvironment.Builder environmentBuilder =  DefaultCouchbaseEnvironment.builder();
        environmentBuilder.queryServiceConfig(QueryServiceConfig.create(0, 12, 1));
        environmentBuilder.keepAliveTimeout(connectTimeout);
        environmentBuilder.connectTimeout(connectTimeout);
        environmentBuilder.socketConnectTimeout(socketTimeout);
        environmentBuilder.computationPoolSize(10);
        return environmentBuilder.build();
    }

    @Override
    public CouchbaseCustomConversions customConversions() {
        return new CouchbaseCustomConversions(Arrays.asList(
                BigDecimalToString.INSTANCE,
                StringToBigDecimalConverter.INSTANCE));
    }

    @WritingConverter
    public static enum BigDecimalToString implements Converter<BigDecimal, String> {
        INSTANCE;

        @Override
        public String convert(BigDecimal source) {
            if (source == null) {
                return null;
            } else {
                return source.toString();
            }
        }
    }

    @ReadingConverter
    public static enum StringToBigDecimalConverter implements Converter<String, BigDecimal> {
        INSTANCE;

        @Override
        public BigDecimal convert(String source) {
            if (source == null) {
                return null;
            } else {
                return new BigDecimal(source);
            }
        }
    }
}
