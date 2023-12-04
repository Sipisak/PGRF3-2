#version 330

out vec4 outColor;

uniform vec3 lightDir; // Směr zdroje světla
uniform vec3 viewDir;  // Směr pohledu kamery
const float shininess = 32.0;

void main() {
    // Normála je vždy (0, 0, 1) ve fragment shaderu
    vec3 norm = normalize(vec3(0.0, 0.0, 1.0));

    // Difúzní složka osvětlení
    float diff = max(dot(norm, lightDir), 0.0);

    // Spekulární složka osvětlení
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);

    // Ambientní složka osvětlení
    float ambient = 0.3; // Nastavte dle potřeby

    // Kombinace difúzní, ambientní a spekulární složky
    lighting = diff + ambient + spec;
    outColor = vec4(lighting, 1.0);
}
