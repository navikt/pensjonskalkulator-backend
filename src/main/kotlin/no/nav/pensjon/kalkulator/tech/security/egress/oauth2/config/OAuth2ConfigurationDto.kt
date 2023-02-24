package no.nav.pensjon.kalkulator.tech.security.egress.oauth2.config

import com.fasterxml.jackson.annotation.JsonProperty

class OAuth2ConfigurationDto {

    @JsonProperty("token_endpoint")
    private var tokenEndpoint: String? = null

    @JsonProperty("token_endpoint_auth_methods_supported")
    private var tokenEndpointAuthMethodsSupported: List<String?>? = null

    @JsonProperty("jwks_uri")
    private var jwksUri: String? = null

    @JsonProperty("response_modes_supported")
    private var responseModesSupported: List<String?>? = null

    @JsonProperty("subject_types_supported")
    private var subjectTypesSupported: List<String?>? = null

    @JsonProperty("id_token_signing_alg_values_supported")
    private var idTokenSigningAlgValuesSupported: List<String?>? = null

    @JsonProperty("response_types_supported")
    private var responseTypesSupported: List<String?>? = null

    @JsonProperty("scopes_supported")
    private var scopesSupported: List<String?>? = null

    @JsonProperty("issuer")
    private var issuer: String? = null

    @JsonProperty("request_uri_parameter_supported")
    private var requestUriParameterSupported: Boolean? = null

    @JsonProperty("userinfo_endpoint")
    private var userinfoEndpoint: String? = null

    @JsonProperty("authorization_endpoint")
    private var authorizationEndpoint: String? = null

    @JsonProperty("device_authorization_endpoint")
    private var deviceAuthorizationEndpoint: String? = null

    @JsonProperty("http_logout_supported")
    private var httpLogoutSupported: Boolean? = null

    @JsonProperty("frontchannel_logout_supported")
    private var frontchannelLogoutSupported: Boolean? = null

    @JsonProperty("end_session_endpoint")
    private var endSessionEndpoint: String? = null

    @JsonProperty("claims_supported")
    private var claimsSupported: List<String?>? = null

    @JsonProperty("tenant_region_scope")
    private var tenantRegionScope: String? = null

    @JsonProperty("cloud_instance_name")
    private var cloudInstanceName: String? = null

    @JsonProperty("cloud_graph_host_name")
    private var cloudGraphHostName: String? = null

    @JsonProperty("msgraph_host")
    private var msgraphHost: String? = null

    @JsonProperty("rbac_url")
    private var rbacUrl: String? = null

    @JsonProperty("token_endpoint")
    fun getTokenEndpoint(): String? {
        return tokenEndpoint
    }

    @JsonProperty("token_endpoint")
    fun setTokenEndpoint(tokenEndpoint: String?) {
        this.tokenEndpoint = tokenEndpoint
    }

    @JsonProperty("token_endpoint_auth_methods_supported")
    fun getTokenEndpointAuthMethodsSupported(): List<String?>? {
        return tokenEndpointAuthMethodsSupported
    }

    @JsonProperty("token_endpoint_auth_methods_supported")
    fun setTokenEndpointAuthMethodsSupported(tokenEndpointAuthMethodsSupported: List<String?>?) {
        this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported
    }

    @JsonProperty("jwks_uri")
    fun getJwksUri(): String? {
        return jwksUri
    }

    @JsonProperty("jwks_uri")
    fun setJwksUri(jwksUri: String?) {
        this.jwksUri = jwksUri
    }

    @JsonProperty("response_modes_supported")
    fun getResponseModesSupported(): List<String?>? {
        return responseModesSupported
    }

    @JsonProperty("response_modes_supported")
    fun setResponseModesSupported(responseModesSupported: List<String?>?) {
        this.responseModesSupported = responseModesSupported
    }

    @JsonProperty("subject_types_supported")
    fun getSubjectTypesSupported(): List<String?>? {
        return subjectTypesSupported
    }

    @JsonProperty("subject_types_supported")
    fun setSubjectTypesSupported(subjectTypesSupported: List<String?>?) {
        this.subjectTypesSupported = subjectTypesSupported
    }

    @JsonProperty("id_token_signing_alg_values_supported")
    fun getIdTokenSigningAlgValuesSupported(): List<String?>? {
        return idTokenSigningAlgValuesSupported
    }

    @JsonProperty("id_token_signing_alg_values_supported")
    fun setIdTokenSigningAlgValuesSupported(idTokenSigningAlgValuesSupported: List<String?>?) {
        this.idTokenSigningAlgValuesSupported = idTokenSigningAlgValuesSupported
    }

    @JsonProperty("response_types_supported")
    fun getResponseTypesSupported(): List<String?>? {
        return responseTypesSupported
    }

    @JsonProperty("response_types_supported")
    fun setResponseTypesSupported(responseTypesSupported: List<String?>?) {
        this.responseTypesSupported = responseTypesSupported
    }

    @JsonProperty("scopes_supported")
    fun getScopesSupported(): List<String?>? {
        return scopesSupported
    }

    @JsonProperty("scopes_supported")
    fun setScopesSupported(scopesSupported: List<String?>?) {
        this.scopesSupported = scopesSupported
    }

    @JsonProperty("issuer")
    fun getIssuer(): String? {
        return issuer
    }

    @JsonProperty("issuer")
    fun setIssuer(issuer: String?) {
        this.issuer = issuer
    }

    @JsonProperty("request_uri_parameter_supported")
    fun getRequestUriParameterSupported(): Boolean? {
        return requestUriParameterSupported
    }

    @JsonProperty("request_uri_parameter_supported")
    fun setRequestUriParameterSupported(requestUriParameterSupported: Boolean?) {
        this.requestUriParameterSupported = requestUriParameterSupported
    }

    @JsonProperty("userinfo_endpoint")
    fun getUserinfoEndpoint(): String? {
        return userinfoEndpoint
    }

    @JsonProperty("userinfo_endpoint")
    fun setUserinfoEndpoint(userinfoEndpoint: String?) {
        this.userinfoEndpoint = userinfoEndpoint
    }

    @JsonProperty("authorization_endpoint")
    fun getAuthorizationEndpoint(): String? {
        return authorizationEndpoint
    }

    @JsonProperty("authorization_endpoint")
    fun setAuthorizationEndpoint(authorizationEndpoint: String?) {
        this.authorizationEndpoint = authorizationEndpoint
    }

    @JsonProperty("device_authorization_endpoint")
    fun getDeviceAuthorizationEndpoint(): String? {
        return deviceAuthorizationEndpoint
    }

    @JsonProperty("device_authorization_endpoint")
    fun setDeviceAuthorizationEndpoint(deviceAuthorizationEndpoint: String?) {
        this.deviceAuthorizationEndpoint = deviceAuthorizationEndpoint
    }

    @JsonProperty("http_logout_supported")
    fun getHttpLogoutSupported(): Boolean? {
        return httpLogoutSupported
    }

    @JsonProperty("http_logout_supported")
    fun setHttpLogoutSupported(httpLogoutSupported: Boolean?) {
        this.httpLogoutSupported = httpLogoutSupported
    }

    @JsonProperty("frontchannel_logout_supported")
    fun getFrontchannelLogoutSupported(): Boolean? {
        return frontchannelLogoutSupported
    }

    @JsonProperty("frontchannel_logout_supported")
    fun setFrontchannelLogoutSupported(frontchannelLogoutSupported: Boolean?) {
        this.frontchannelLogoutSupported = frontchannelLogoutSupported
    }

    @JsonProperty("end_session_endpoint")
    fun getEndSessionEndpoint(): String? {
        return endSessionEndpoint
    }

    @JsonProperty("end_session_endpoint")
    fun setEndSessionEndpoint(endSessionEndpoint: String?) {
        this.endSessionEndpoint = endSessionEndpoint
    }

    @JsonProperty("claims_supported")
    fun getClaimsSupported(): List<String?>? {
        return claimsSupported
    }

    @JsonProperty("claims_supported")
    fun setClaimsSupported(claimsSupported: List<String?>?) {
        this.claimsSupported = claimsSupported
    }

    @JsonProperty("tenant_region_scope")
    fun getTenantRegionScope(): String? {
        return tenantRegionScope
    }

    @JsonProperty("tenant_region_scope")
    fun setTenantRegionScope(tenantRegionScope: String?) {
        this.tenantRegionScope = tenantRegionScope
    }

    @JsonProperty("cloud_instance_name")
    fun getCloudInstanceName(): String? {
        return cloudInstanceName
    }

    @JsonProperty("cloud_instance_name")
    fun setCloudInstanceName(cloudInstanceName: String?) {
        this.cloudInstanceName = cloudInstanceName
    }

    @JsonProperty("cloud_graph_host_name")
    fun getCloudGraphHostName(): String? {
        return cloudGraphHostName
    }

    @JsonProperty("cloud_graph_host_name")
    fun setCloudGraphHostName(cloudGraphHostName: String?) {
        this.cloudGraphHostName = cloudGraphHostName
    }

    @JsonProperty("msgraph_host")
    fun getMsgraphHost(): String? {
        return msgraphHost
    }

    @JsonProperty("msgraph_host")
    fun setMsgraphHost(msgraphHost: String?) {
        this.msgraphHost = msgraphHost
    }

    @JsonProperty("rbac_url")
    fun getRbacUrl(): String? {
        return rbacUrl
    }

    @JsonProperty("rbac_url")
    fun setRbacUrl(rbacUrl: String?) {
        this.rbacUrl = rbacUrl
    }

}
