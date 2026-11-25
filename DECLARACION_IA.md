# Declaración de uso de herramientas de inteligencia artificial

**Proyecto:** NotiHub (Parcial de Patrones de Diseño, Propuesta 4)
**Herramienta utilizada:** Claude (Anthropic), mediante la interfaz de chat claude.ai.
**Periodo de uso:** del 15 al 22 de noviembre de 2026.

Esta declaración indica qué partes del proyecto se hicieron con ayuda de IA, para qué se usó y qué hice yo en cada una. Todo el contenido de este repositorio lo puedo explicar.

## 1. Resumen por parte del proyecto

| Parte del proyecto | Qué hizo la IA | Qué hice yo |
|---|---|---|
| Elección de la propuesta | Comparó las cinco propuestas del enunciado y recomendó la 4 por tener menos entidades de dominio y patrones claros. | Analice las opciones y termine escogiendo en base a las recomendaciones, estuve de acuerdo gracias a que era facil de implementar y provar esa propuesta |
| Alcance y requisitos | Redactó un borrador del alcance, las funcionalidades excluidas, las áreas de cambio frecuente y los requisitos funcionales y no funcionales. | Definí las decisiones técnicas que condicionaron el alcance (NetBeans, Maven, interfaz Swing, nombres en español y datos guardados en archivo). Ademas de redactar los requisitos funcionales y no funcionales en base al borrador y lo que requeria el sistema conforme se iba desarrollando |
| Problemas de diseño y elección de patrones | Propuso los problemas de diseño, los patrones (Adapter, Facade, Strategy y Observer), las alternativas descartadas y la arquitectura por paquetes. | Busque proyectos similares y analice si los patrones sugeridos podrian ser aplicados y como deberian ser aplicados |
| Código del núcleo y pruebas (paquetes `modelo`, `canales`, `reglas`, `registro`, `nucleo`, `persistencia`) | Generó el código en versiones sencillas, junto con sus pruebas unitarias con JUnit 5. | Copié cada versión al proyecto, la compilé, ejecuté las pruebas en NetBeans y corregí problemas de configuración y ubicación de archivos (el `pom.xml`, el paquete donde estaban las clases y clases duplicadas). Tras realizar cada prueba, observe que los patrones fueran aplicados correctamente en las correciones de los comits y el avance que tenia el proyecto para completarse |
| Menú de consola | Generó `MenuConsola`, `Sistema` y `Principal`. | Ejecuté el programa y corregi segun las necesidades de los requerimientos |
| Interfaz gráfica (paquete `interfaz.grafica`) | Generó la base de la ventana Swing, el modelo de la tabla del historial y el observador de la ventana. | Ajuste la interfaz y las funciones que esta debia ejecutar |
| Diagramas UML | Sugeri los programas para realizar los diagramas | Realice los diagramas, revisé que coincidan con el código y los ajuste manualmente para su comprencion visual |
| Documento de diseño | Redactó un borrador con la estructura y el contenido técnico de cada patrón. | En base al conocimiento de realizar el programa y hacer las respectivas correcciones y ejecuciones, redacte el documento tecnico |
| Uso de Git y GitHub | Explicó los comandos y ayudó a diagnosticar errores. | Configuré mi cuenta, creé el repositorio, hice los commits y los subí. |

## 2. Código y contenido de otras fuentes

- **Librerías:** el código usa solo el JDK (incluido Swing) y JUnit 5 para las pruebas.
- **Otras fuentes:** no copié código, diagramas ni texto de otros repositorios, sitios o compañeros.

## 3. Cómo se verificó el código generado

- Las pruebas unitarias se ejecutan con `mvn test`: 94 pruebas en total. 
- Ejecuté el programa en consola y en interfaz gráfica. ademas de realizar pruebas para verificar que este funcionando correctamente
- La IA no sustituye mi revisión: el código y los textos generados pueden contener errores, y yo respondo por lo que entrego.

## 4. Declaración

Declaro que, en lo que me corresponde, el trabajo entregado refleja mi comprensión del diseño y que puedo explicar cualquier parte del código, de los diagramas y del documento, y ampliar el sistema ante un cambio de requisito.

[Alex Santiago Niño Castañeda] 
