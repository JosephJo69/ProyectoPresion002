Link video proyecto fase 1: https://drive.google.com/file/d/1CR6qZBaGpGNjU1ihEbvlxmAYKEFY-iZV/view?usp=sharing

Descripción del proyecto

El proyecto consiste en una aplicación móvil Android diseñada para registrar, analizar y gestionar lecturas de presión arterial sistólica y diastólica. El usuario ingresa sus 
valores de presión y edad, y la aplicación se conecta con la API de Gemini (IA de Google) para generar de forma inmediata un análisis personalizado y recomendaciones de salud
relacionadas con el control de la presión arterial.

La aplicación también incorpora un historial de lecturas donde se almacenan los registros anteriores del usuario, permitiéndole observar su evolución. Además, está integrada con n8n, 
una herramienta de automatización que permite:

Guardar automáticamente cada lectura en Google Sheets como respaldo o para análisis.

Generar y almacenar reportes en PDF con las recomendaciones entregadas por Gemini.

Este proyecto busca ofrecer una solución práctica, educativa y accesible para el monitoreo personal de la salud cardiovascular.

Instrucciones de instalación

Requisitos previos
Android Studio instalado (versión Electric Eel o superior).

Conexión a internet para usar la API de Gemini y el flujo n8n.

Cuenta de Google para la integración con Google Sheets (vía n8n).

Servidor de n8n activo (en local o nube, como n8n.io o Render).

Instalación de la aplicación
Clonar o descargar el proyecto desde el repositorio:

git clone https://github.com/usuario/lector-presion.git

Abrir el proyecto en Android Studio.

Asegurarse de tener configurado el SDK de Android 14 (API 34).

En el archivo AndroidManifest.xml, verificar los permisos de internet:

En el archivo build.gradle (Module), incluir las dependencias necesarias:

implementation 'com.android.volley:volley:1.2.1' implementation 'org.json:json:20210307'

Ejecutar la aplicación en un emulador o dispositivo físico.

Configuración de la API de Gemini
Crear una clave API en la Google AI Studio (Gemini API).

Guardar la clave en el archivo strings.xml:

TU_API_KEY

En el código Java/Kotlin, realizar las peticiones HTTP POST a Gemini para analizar las lecturas y recibir recomendaciones.

Explicación de la integración con n8n

La integración con n8n permite automatizar la generación de reportes de las lecturas tomadas por la aplicación.

🔹 Flujo principal implementado

Objetivo: Guardar el análisis generado con Gemini y convertirlo en un archi PDF descargable.

Nodos del flujo:

Webhook (POST) → recibe los datos enviados por la app.

Code in Javascript: Bloque de codigo con la funcion para descargar PDF sobre el análisis realizado por Gemini.

Respond to Webhook → confirma el guardado a la app.

Requisitos y dependencias 
🔸 En la aplicación Android: Dependencia Descripción Volley (com.android.volley:volley) Para realizar solicitudes HTTP a 
la API de Gemini y al Webhook de n8n. org.json Para manejar y construir objetos JSON en las peticiones y respuestas. Material Components: Para la 
interfaz visual (botones, campos, etc.). Android SDK 33+: Compatible con las versiones modernas de Android. 
🔸 En n8n: Dependencia Descripción Webhook Node: Recibe datos desde la app Android. Code in Javascript Node: Bloque de codigo para realizar la generación de archivo PDF. Respond to Webhook Node: Envía confirmaciones o respuestas a la app.
