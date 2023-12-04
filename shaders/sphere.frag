#version 330

in vec2 texCoord;
out vec4 outColor;

uniform sampler2D textureEarth;


const vec3 ambientColor = vec3(0.1, 0.1, 0.1);
const vec3 diffuseColor = vec3(1.0, 1.0, 1.0);
const vec3 specularColor = vec3(1.0, 1.0, 1.0);
const float shininess = 32.0;


const vec3 lightPos = vec3(0.0, 0.0, 5.0);

void main() {
    vec4 texColor = texture(textureEarth, texCoord);


    vec3 fragPos = vec3(1.0, 1.0, 0.0);
    vec3 normal = normalize(vec3(0.0, 0.0, 1.0));


    vec3 lightDir = normalize(lightPos - fragPos);


    float diff = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = diff * diffuseColor;


    vec3 viewDir = normalize(-fragPos);
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
    vec3 specular = spec * specularColor;


    vec3 result = (ambientColor + diffuse + specular) * texColor.rgb;

    outColor = vec4(result, 1.0);
}
