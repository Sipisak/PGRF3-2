#version 330

in vec2 inPosition;
out vec3 color;
out vec2 TexCoord;

uniform mat4 uView;
uniform mat4 uProj;
uniform mat4 uTransform;
uniform float uTime;


float rand(vec2 co){
    return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453) ;
}

void main(){

    float radius = 1.0;

    float theta = inPosition.x * 6.28;
    float phi = inPosition.y * 3.14;

    float x = radius * sin(phi) * cos(theta);
    float y = radius * sin(phi) * sin(theta);
    float z = radius * cos(phi);

    vec3 position = vec3(x, y, z);
    float noise = rand(inPosition * uTime);
    float displacement = noise * 0.75;

    vec3 newPosition = position + normalize(position) * displacement;
    gl_Position = uProj * uView * vec4(newPosition, 1.0);

    color = vec3(1.0, 1.0 - noise, 1.0 - noise);

    TexCoord = inPosition * 0.5 + 0.5;
}
