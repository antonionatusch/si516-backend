# SaludConecta

## Facultad de Ingeniería  
**Proyecto Final**  
Materia: Sistemas Inteligentes e Innovación (SI516)  
Docente: Ing. Carlos Wilfredo Egüez Terrazas  


## Introducción

**SaludConecta** es un sistema inteligente diseñado para optimizar la experiencia hospitalaria en un centro médico local. El proyecto busca resolver problemáticas comunes relacionadas con la falta de información clara, la desorientación dentro del hospital y los largos tiempos de espera, que afectan tanto a pacientes como a visitantes. Estas dificultades generan frustración y una experiencia poco satisfactoria.

Con el objetivo de mejorar la interacción de los usuarios con el hospital, la solución propuesta integra herramientas tecnológicas para ofrecer orientación, información en tiempo real y gestión eficiente de tiempos de atención. El proyecto fue desarrollado como parte de la materia **Sistemas Inteligentes e Innovación (SI516)**, siguiendo la metodología **Design Thinking** para garantizar que las soluciones respondan a las necesidades reales.

---

## Características Técnicas

### Backend
El backend principal fue desarrollado en **Java** utilizando **Spring Boot**. Se encargó de la gestión de datos, integración con servicios cognitivos y controladores REST.

#### Funcionalidades Clave:
1. Gestión de pacientes, turnos, recetas, y documentos médicos.
2. Procesamiento asíncrono para tareas como transcripción de audio.
3. Integración con un servicio remoto de transcripción desarrollado con **Whisper.cpp** y **Llama 3.1 8b**.
4. Almacenamiento seguro de archivos médicos utilizando **MongoDB GridFS**.

#### Configuración del Backend
El archivo `application-example.properties` contiene un ejemplo de configuración necesario para ejecutar el backend. Los pasos son los siguientes:
1. Copiar el archivo `application-example.properties` y renombrarlo como `application.properties`:
   ```bash
   cp src/main/resources/application-example.properties src/main/resources/application.properties
   ```
2. Editar el archivo `application.properties` recién creado y settear los valores según los parámetros deseados, como:
   - La URI de MongoDB.
   - La URL del servicio remoto de transcripción.
   - Las credenciales de JWT.
   - Los límites de tamaño para la subida de archivos.

**Nota:** Configure las propiedades según las necesidades de su entorno. Asegúrese de incluir todos los valores obligatorios antes de ejecutar el backend.

### Servicio Cognitivo con Whisper.cpp y Llama 3.1 8b
El sistema utiliza un servicio remoto de transcripción basado en **Whisper.cpp** y **Llama 3.1 8b**, desarrollado en Python. Este servicio realiza las siguientes tareas:
1. Descarga del audio desde el backend.
2. Conversión de audio `.webm` a `.wav` (16 kHz mono) y división en fragmentos por silencios.
3. Transcripción del audio utilizando **Whisper**.
4. Extracción de información estructurada en formato JSON mediante un modelo alojado en **LM Studio**.
5. Exposición de resultados a través de endpoints.

**Nota:** Este servicio es intensivo en recursos y se recomienda ejecutarlo en una máquina separada con capacidades adecuadas. Requiere tener instalado **Whisper.cpp**, **Llama 3.1 8b Instruct** y **LM Studio**. El script que implementa esta funcionalidad se encuentra en el archivo `transcriber_service.py`.

### Arquitectura General
El sistema sigue una arquitectura modular basada en:
- **Backend:** Java con Spring Boot.
- **Base de datos:** MongoDB con GridFS.
- **Frontend:** Simulado con React y CoreUI para interfaces de prueba.
- **Servicio Externo:** Whisper.cpp para procesamiento de audio.

---

## Instalación y Configuración

### Requisitos Previos
1. **Java JDK 17** o superior.
2. **MongoDB** instalado y configurado.
3. Acceso al servicio remoto de transcripción.

### Pasos de Instalación

#### 1. Instalación de Backend
1. Clonar este repositorio:
   ```bash
   git clone https://github.com/antonionatusch/si516-backend.git
   cd si516-backend
   ```
2. Configurar `application.properties` (ver sección anterior).
3. Compilar y ejecutar el backend:
   ```bash
   ./mvnw spring-boot:run
   ```

#### 2. Verificación
- Asegúrese de que el backend y el servicio remoto de transcripción estén en ejecución.
- Realice una prueba enviando un archivo de audio al endpoint correspondiente.

---

## Ejecución del Proyecto
1. Iniciar el backend con:
   ```bash
   ./mvnw spring-boot:run
   ```
2. Asegurarse de que el servicio de transcripción remoto esté disponible.
3. Realizar solicitudes al sistema siguiendo la documentación de los endpoints.

---

## Demo de Transcripción de Audio
En esta sección se incluirá un video demostrativo mostrando la funcionalidad del sistema, específicamente el proceso de transcripción de audio y la extracción de información estructurada.

---

## Créditos y Agradecimientos

### Equipo de Desarrollo
- Lipsy Dalire Cardona Durán - a2022111834@estudiantes.upsa.edu.bo - [@lileeex](https://github.com/lileeex)
- Carlos Augusto Egüez Bazán - a2022112318@estudiantes.upsa.edu.bo - [@Aeguez233](https://github.com/Aeguez233)
- Camila Letizia Ortiz Vasquez - a2022112491@estudiantes.upsa.edu.bo - [@LetiziaOrtizVasq](https://github.com/LetiziaOrtizVasq)
- Paul Fernando Vino Herrera - a2022211405@estudiantes.upsa.edu.bo - [@paulfer03](https://github.com/paulfer03)
- Antonio Miguel Natusch Zarco - a2022111958@estudiantes.upsa.edu.bo - [@antonionatusch](https://github.com/antonionatusch)

### Agradecimientos Especiales
Un especial agradecimiento a nuestro docente, **Ing. Carlos Wilfredo Egüez Terrazas** (carloseguez@upsa.edu.bo), por su constante apoyo y recomendaciones durante el desarrollo del proyecto.

---

## Licencia
Este proyecto fue desarrollado con fines académicos y está sujeto a la licencia MIT para su distribución y uso.
