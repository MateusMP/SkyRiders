#version 150

struct LightProperties
{
	vec4 position;
	vec4 ambientColor;
	vec4 diffuseColor;
	vec4 specularColor;
};

struct MaterialProperties
{
	vec4 ambientColor;
	vec4 diffuseColor;
	vec4 specularColor;
	float specularExponent;
};

uniform	LightProperties u_light;
uniform	MaterialProperties u_material;
uniform mat4 u_viewMatrix;
uniform mat4 u_modelMatrix;

uniform sampler2D u_texture;
uniform bool u_is_texture;


in vec2 v_texcoord;
in vec3 v_normal;		// Surface normal
in vec3 toLightVector;
in vec3 toCameraVector;

out vec4 fragColor;

void main(void)
{
	vec4 finalAmbientColor = u_light.ambientColor * u_material.ambientColor;

	vec3 unitNormal = normalize(v_normal);
	vec3 unitLightVector = normalize(toLightVector);
	vec3 unitToCamera = normalize(toCameraVector);
	vec3 lightDirection = -unitLightVector;
	
	float nDotl = dot(unitNormal, unitLightVector);
	float brightness = max(nDotl, 0.0);
	vec4 diffuse = brightness * u_light.diffuseColor * u_material.diffuseColor;
	
	vec4 texColor = vec4(1.0, 1.0, 1.0, 1.0);
	if (u_is_texture) {
		texColor = texture(u_texture, v_texcoord);
		if (texColor.a < 0.1)
			discard;
	}
	
	vec3 reflectLightDirection = reflect(lightDirection, unitNormal);
	float specularFactor = dot(reflectLightDirection, unitToCamera);
	specularFactor = max(specularFactor, 0.0);
	float specularIntensity = pow(specularFactor, u_material.specularExponent);
	
	vec4 finalSpecular = u_material.specularColor * u_light.specularColor * specularIntensity;
	
	fragColor = (diffuse + finalSpecular + finalAmbientColor) * texColor;
}
