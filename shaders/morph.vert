#version 330

in vec2 inPosition;
out vec3 color;

uniform mat4 uView;
uniform mat4 uProj;
uniform float uTime;

const float PI = 3.14159265359;

vec3 color1 = vec3(1.f, 0.f, 0.f);  // Red
vec3 color2 = vec3(0.f, 0.f, 1.f);  // Blue

vec3 spheretocartezian(float R, float azimut, float zenit){
    float x = R * sin(zenit) * cos(azimut);
    float y = R * sin(zenit) * sin(azimut);
    float z = R * cos(zenit);
    return vec3(x, y, z);
}

vec3 fce(float azimut, float zenit){
    float R = 3 * cos(4 * azimut);
    return vec3(R / 3, zenit, azimut);
}

void main(){
    vec3 position = vec3(inPosition, 0.f);
    float zenit = position.x * PI;
    float azimut = position.y * 2.0 * PI;

    vec3 pos1 = spheretocartezian(1.f, azimut, zenit);
    vec3 pos = fce(azimut, zenit);
    vec3 pos2 = spheretocartezian(pos.x, pos.z, pos.y);

    float alpha = 0.5 * (sin(uTime / 500.0) + 1.0);
    vec3 result = mix(pos2, pos1, alpha);

    gl_Position = uProj * uView * vec4(result, 1.0);

    color = mix(color1, color2, alpha);
}
