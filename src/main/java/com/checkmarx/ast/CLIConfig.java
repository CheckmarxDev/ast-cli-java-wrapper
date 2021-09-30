package com.checkmarx.ast;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Builder
public class CLIConfig {

    private static final Pattern pattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");

    private String baseUri;
    private String baseAuthUri;
    private String tenant;
    private String clientId;
    private String clientSecret;
    private String apiKey;
    private String pathToExecutable;
    @Setter(AccessLevel.NONE)
    private List<String> additionalParameters;

    public void validate() throws InvalidCLIConfigException {
        if (StringUtils.isBlank(getBaseUri())) {
            throw new InvalidCLIConfigException("checkmarx server URL was not set");
        }

        if (StringUtils.isBlank(getApiKey())
            && (StringUtils.isBlank(getClientId()) || StringUtils.isBlank(getClientSecret()))) {
            throw new InvalidCLIConfigException("credentials were not set");
        }
    }

    public void setAdditionalParameters(String additionalParameters) {
        this.additionalParameters = parseAdditionalParameters(additionalParameters);
    }

    public List<String> toArguments() {
        List<String> commands = new ArrayList<>();

        if (StringUtils.isNotBlank(getClientId()) && StringUtils.isNotBlank(getClientSecret())) {
            commands.add("--client-id");
            commands.add(getClientId());
            commands.add("--client-secret");
            commands.add(getClientSecret());
        } else if (StringUtils.isNotBlank(getApiKey())) {
            commands.add("--apikey");
            commands.add(getApiKey());
        }

        if (StringUtils.isNotBlank(getTenant())) {
            commands.add("--tenant");
            commands.add(getTenant());
        }

        commands.add("--base-uri");
        commands.add(getBaseUri());

        if (StringUtils.isNotBlank(getBaseAuthUri())) {
            commands.add("--base-auth-uri");
            commands.add(getBaseAuthUri());
        }

        commands.addAll(getAdditionalParameters());

        return commands;
    }

    public static final class InvalidCLIConfigException extends Exception {
        public InvalidCLIConfigException(String message) {
            super(message);
        }
    }

    @SuppressWarnings("ALL")
    public static class CLIConfigBuilder {
        private List<String> additionalParameters;

        public CLIConfigBuilder additionalParameters(String additionalParameters) {
            this.additionalParameters = parseAdditionalParameters(additionalParameters);
            return this;
        }
    }

    public static List<String> parseAdditionalParameters(String additionalParameters) {
        List<String> additionalParametersList = new ArrayList<>();
        if (StringUtils.isNotBlank(additionalParameters)) {
            Matcher m = pattern.matcher(additionalParameters);
            while (m.find()) {
                additionalParametersList.add(m.group(1));
            }
        }
        return additionalParametersList;
    }
}
