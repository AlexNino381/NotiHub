# NotiHub

Sistema de notificaciones multicanal en Java. Envía mensajes a usuarios por correo, SMS y mensajería instantánea (canales simulados), respeta las preferencias de cada usuario y registra cada envío. Tiene una versión de consola y una interfaz gráfica que usan el mismo núcleo.

Proyecto del parcial de Patrones de Diseño (Propuesta 4: Sistema de notificaciones multicanal).

## Patrones de diseño aplicados

| Patrón | Categoría | Problema que resuelve | Clases principales |
|---|---|---|---|
| **Adapter** | Estructural | Cada servicio externo tiene una interfaz distinta; se unifican detrás de una sola. | `CanalNotificacion`, `AdaptadorCorreo`, `AdaptadorSms`, `AdaptadorMensajeria` |
| **Facade** | Estructural | Las interfaces necesitan una sola operación para enviar sin conocer el subsistema. | `CentralNotificaciones` |
| **Strategy** | Comportamiento | Las reglas de envío (urgente, informativo) cambian y no deben vivir en un `if`. | `EstrategiaEnvio`, `EnvioUrgente`, `EnvioInformativo` |
| **Observer** | Comportamiento | El registro de envíos puede ir a destinos distintos sin acoplar el núcleo a ellos. | `ObservadorEnvio`, `HistorialEnvios`, `RegistroArchivo`, `RegistroConsola`, `RegistroAreaTexto` |

La justificación completa de cada patrón (problema, alternativa descartada, ventajas, desventajas y relación con SOLID) está en el documento de diseño, carpeta `docs/`.

## Requisitos para ejecutarlo

- JDK 17 o superior
- Maven 3.6.3 o superior (viene incluido en NetBeans)
- No necesita conexión a internet para ejecutarse. Maven la necesita la primera vez para descargar JUnit.

## Compilar y probar

```
mvn test
```

Debe terminar con `Tests run: 94, Failures: 0` y `BUILD SUCCESS`.

## Ejecutar

Primero compila:

```
mvn compile
```

**Versión de consola:**

```
java -cp target/classes notihub.Principal
```

Argumento opcional: la probabilidad inicial de fallo de los canales simulados, entre 0.0 y 1.0 (por defecto 0.0). Ejemplo: `java -cp target/classes notihub.Principal 0.3`

**Versión gráfica:**

```
java -cp target/classes notihub.PrincipalGrafico
```

**Desde NetBeans:** clic derecho sobre `Principal.java` o `PrincipalGrafico.java` y *Run File*.

Ambas versiones guardan sus datos en la carpeta `datos/` del directorio desde donde se ejecutan, así que comparten usuarios e historial. La primera vez se crean dos usuarios de ejemplo (Ana y Luis).

## Cómo usarlo

**Consola:** menú numerado para ver y registrar usuarios, enviar mensajes, crear plantillas, consultar el historial con filtros y cambiar la probabilidad de fallo.

**Interfaz gráfica:** cuatro pestañas.

- *Enviar*: elegir usuario, plantilla y tipo de mensaje (urgente o informativo); ver el resultado de cada canal; ajustar la probabilidad de fallo.
- *Usuarios*: ver, registrar y eliminar usuarios, con sus canales en orden de preferencia.
- *Plantillas*: ver y crear plantillas con marcadores `{nombre}`, `{correo}`, `{telefono}` y `{fecha}`.
- *Historial*: consultar los envíos filtrando por usuario, canal y estado.

## Reglas de envío

- **Urgente:** se envía por todos los canales que el usuario acepta.
- **Informativo:** se envía solo por el canal de mayor preferencia del usuario.
- **Canales no aceptados:** nunca se usan. Si el usuario no acepta ninguno, el envío se registra como fallido con su motivo.
- **Reintentos:** si un canal falla, se reintenta hasta 3 veces. El registro guarda cuántos intentos hicieron falta.
- **Un canal que falla no detiene a los demás.**

## Estructura del proyecto

```
src/main/java/notihub/
├── modelo/          Usuario, Mensaje, Plantilla, TipoCanal, TipoMensaje
├── canales/         CanalNotificacion (Adapter) y los tres adaptadores, ResultadoEnvio
│   └── externos/    Servicios simulados de correo, SMS y mensajería
├── reglas/          EstrategiaEnvio (Strategy), EnvioUrgente, EnvioInformativo
├── registro/        ObservadorEnvio (Observer), historial, archivo y consola
├── nucleo/          CentralNotificaciones (Facade), Sistema, AgendaUsuarios, CatalogoPlantillas
├── persistencia/    RepositorioUsuarios (archivo CSV)
├── interfaz/
│   ├── consola/     MenuConsola
│   └── grafica/     VentanaPrincipal, PanelNotiHub, RegistroAreaTexto, ModeloTablaHistorial
├── Principal.java           Punto de entrada de la consola
└── PrincipalGrafico.java    Punto de entrada de la interfaz gráfica
src/test/java/notihub/       Pruebas unitarias (JUnit 5), por paquete
docs/                        Documento de diseño y diagramas UML
datos/                       Se crea al ejecutar (usuarios.csv e historial.csv); no se versiona
```

Regla de dependencia: las interfaces (`consola` y `grafica`) solo conocen el núcleo (`nucleo`); no contienen lógica de negocio. `Sistema` es el único lugar que conoce las clases concretas de canales, servicios y observadores.

## Cómo extenderlo

| Cambio | Qué se agrega | Qué no se modifica |
|---|---|---|
| Un canal nuevo (por ejemplo, Telegram) | Un servicio, un adaptador, un valor en `TipoCanal` y su registro en `Sistema.iniciar` | La fachada y las reglas |
| Una regla de envío nueva | Una clase que implemente `EstrategiaEnvio` y `registrarEstrategia` (más un valor en `TipoMensaje` si es un tipo nuevo) | La fachada |
| Un destino nuevo para el registro | Una clase que implemente `ObservadorEnvio` y `agregarObservador` | La fachada |

## Pruebas

94 pruebas unitarias con JUnit 5. Los canales son simulados, por lo que las pruebas son deterministas: con probabilidad de fallo 0.0 nunca falla y con 1.0 siempre falla, y un canal de prueba falla un número exacto de veces para verificar los reintentos. La ventana gráfica no se prueba automáticamente; sí se prueban su modelo de tabla y su observador.

## Decisiones y limitaciones conocidas

- Los reintentos están en la fachada por simplicidad (la alternativa era un Decorator). No distinguen fallos pasajeros de permanentes, por lo que un usuario sin teléfono también se reintenta.
- Las plantillas creadas en ejecución no se guardan en archivo; los usuarios y el historial sí.
- Los archivos CSV usan `;` como separador; el texto libre se limpia de `;` y saltos de línea.
- Los observadores se notifican de forma síncrona; un observador que falla se ignora para no detener los envíos.

## Alcance

### Funcionalidades que se implementan

| Funcionalidad | Alcance concreto |
|---|---|
| Canales de envío | Tres canales simulados: correo, SMS y mensajería instantánea. Cada uno con su propio formato y su propia forma de "conectarse", como si fueran servicios externos distintos. |
| Envío simplificado | Una sola operación (`enviar`) para que el resto del sistema mande un mensaje sin conocer los canales. |
| Reglas de envío | Urgente (todos los canales que el usuario acepta), informativo (un solo canal) y reintento automático si falla. |
| Plantillas y personalización | Plantillas con marcadores (por ejemplo `{nombre}`, `{fecha}`) que se completan con los datos de cada usuario. |
| Preferencias de usuario | Cada usuario define qué canales acepta y en qué orden de prioridad. |
| Registro de mensajes | Historial de mensajes enviados y fallidos, con consulta y filtros básicos. |
| Interfaces | Consola y GUI sencilla (Swing) que usan el mismo núcleo. |

### Funcionalidades que quedan fuera, y por qué

| Funcionalidad excluida | Razón |
|---|---|
| Conexión a servicios reales (SMTP, pasarelas de SMS, WhatsApp) | Se pide explícitamente simular los canales. Los servicios reales añaden credenciales, costos y dependencias que no aportan al objetivo del parcial. |
| Login y autenticación | No es relevante para el diseño con patrones y consume tiempo de desarrollo. |
| Programación de envíos a futuro | Requiere manejo de hilos y planificación, que desvían el foco del diseño. |
| Adjuntos y contenido multimedia | Complica los formatos de cada canal sin aportar una variación de diseño nueva. |
| Base de datos | Basta con archivos CSV. Una base de datos añade configuración sin aportar valor al diseño. |
| Aplicación web | No es obligatoria ni da puntos adicionales. |
| Estadísticas y reportes avanzados | El registro con filtros es suficiente, y el tiempo se prioriza en patrones, pruebas y documentación. |

### Áreas con variación o cambio frecuente

1. **Canales:** pueden aparecer canales nuevos (Telegram, push, etc.), cada uno con formato y conexión distintos.
2. **Reglas de envío:** pueden surgir políticas nuevas (por franja horaria, por prioridad, con escalamiento).
3. **Plantillas y formato del mensaje:** cambian los tipos de mensaje, los marcadores y el contenido según el canal.
4. **Registro de envíos:** el destino del registro (memoria, archivo, consola) puede variar o ampliarse.

## Requisitos

### Requisitos funcionales

- **RF-01.** El sistema permite registrar usuarios con nombre, correo, teléfono e identificador de mensajería.
- **RF-02.** Cada usuario puede definir los canales que acepta y su orden de preferencia.
- **RF-03.** El sistema ofrece una única operación para enviar un mensaje a un usuario, sin exigir conocer los canales.
- **RF-04.** El sistema soporta tres canales simulados (correo, SMS y mensajería), cada uno con formato propio: el correo con asunto y cuerpo, el SMS con límite de 160 caracteres y la mensajería con texto corto.
- **RF-05.** Un mensaje **urgente** se envía por todos los canales que el usuario acepta.
- **RF-06.** Un mensaje **informativo** se envía solo por el canal de mayor preferencia del usuario.
- **RF-07.** Si un envío falla, el sistema reintenta hasta un número máximo de veces (por defecto 3) antes de marcarlo como fallido.
- **RF-08.** Los canales simulados pueden fallar de forma controlada (probabilidad configurable) para demostrar los reintentos.
- **RF-09.** El sistema permite crear y usar plantillas de mensaje con marcadores de reemplazo.
- **RF-10.** El sistema personaliza cada mensaje con los datos del usuario destinatario.
- **RF-11.** El sistema nunca envía por un canal que el usuario no acepta. Si no acepta ninguno, el mensaje se registra como fallido con el motivo.
- **RF-12.** El sistema registra cada envío con fecha, usuario, canal, estado (enviado o fallido) y número de intentos.
- **RF-13.** El sistema permite consultar el historial filtrando por usuario, canal o estado.
- **RF-14.** El sistema ofrece una interfaz de consola y una interfaz gráfica que usan el mismo núcleo.

### Requisitos no funcionales

- **RNF-01. Extensibilidad:** agregar un canal, una regla de envío o una plantilla nueva no debe obligar a modificar el código existente de la lógica de negocio.
- **RNF-02. Mantenibilidad:** el código se organiza en paquetes con responsabilidades claras y sigue los principios SOLID.
- **RNF-03. Desacoplamiento:** la consola y la GUI no contienen lógica de negocio y dependen solo del núcleo.
- **RNF-04. Testabilidad:** las clases donde se aplica cada patrón tienen pruebas unitarias (JUnit), apoyadas en que los canales son simulados.
- **RNF-05. Fiabilidad:** el fallo de un canal no detiene el envío por los demás ni cierra la aplicación.
- **RNF-06. Usabilidad:** la GUI permite enviar un mensaje en pocos pasos, y los errores se muestran con mensajes claros.
- **RNF-07. Rendimiento:** cada envío simulado responde en menos de 2 segundos en condiciones normales.
- **RNF-08. Portabilidad:** funciona en Java 17 o superior, sin librerías externas salvo JUnit.
- **RNF-09. Trazabilidad:** todo envío, exitoso o fallido, queda registrado.
- **RNF-10. Documentación y control de versiones:** repositorio Git con README de compilación y ejecución, y commits frecuentes.

## Uso de herramientas de inteligencia artificial

Se usó Claude (Anthropic) como apoyo. El detalle de qué se usó y para qué está en [`DECLARACION_IA.md`](DECLARACION_IA.md).

## Autoría

[Alex Santiago Niño Castañeda]
