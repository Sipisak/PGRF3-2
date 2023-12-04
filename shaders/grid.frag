#version 330
in vec3 toLightVector;
in vec3 normal;
in vec2 texCoord;
in vec3 debugColor;
in float lightDistance;


uniform sampler2D textureBricks;
out vec4 outColor;


vec4 ambientColor = vec4(0.9f, 0.4f, 0.f, 1.f);
vec4 diffuseColor = vec4(0.9f, 0.9f, 0.9f, 1.f);

float constantAttenuation = 0.4;
float linearAttenuation = 0.1;
float quadraticAttenuation = 0.001;


void main() {
    vec4 baseColor = vec4(0.2f, 0.7f, 0.f, 1.f);
    //vec4 basecolor =texture(textureBricks, texCoord);

    //diffuse
    vec3 l = normalize(toLightVector);
    vec3 n = normalize(normal);
    float NDotl = max(dot(n, l), 0.);

    vec4 ambient = ambientColor;
    vec4 diffuse = NDotl * diffuseColor;
    vec4 specular = vec4(0);

    float att = 1.0;

    outColor = (ambient + att * (diffuse + specular)) * baseColor;
    //outColor = vec4(debugColor, 1.f);
}

