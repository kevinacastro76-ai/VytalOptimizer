# VytalOptimizer

**VytalOptimizer** es un plugin de diagnóstico para servidores Minecraft **Spigot, Paper y Purpur**, desde Minecraft **1.20 en adelante**. Consulta métricas disponibles directamente desde el servidor: memoria JVM, mundos, entidades, chunks, plugins instalados y tareas pendientes.

> **Importante:** esta versión es una herramienta de diagnóstico. No modifica configuraciones, no elimina entidades ni desactiva plugins. Sus resultados son indicadores para investigar, no una prueba automática de qué plugin causa lag.

## Características

- Diagnóstico general del servidor.
- TPS nativos cuando la plataforma los expone.
- Intervalo aproximado entre ejecuciones del monitor.
- Estadísticas de heap y memoria no-heap de la JVM.
- Conteo de entidades y chunks cargados por mundo.
- Lista de plugins, versiones y estado.
- Conteo de tareas pendientes agrupadas por plugin propietario.
- Comandos en juego y consola.
- Configuración YAML y alias `/vo`.

## Requisitos

- Minecraft 1.20.x o posterior.
- Spigot, Paper o Purpur.
- Java 21 o superior para servidores modernos.
- Permiso `vytaloptimizer.admin` (por defecto, operadores).

## Instalación

1. Descarga `VytalOptimizer-1.0.0.jar` desde los artefactos de GitHub Actions.
2. Coloca el JAR en la carpeta `plugins/`.
3. Inicia o reinicia el servidor.
4. Revisa la consola para confirmar que el plugin se habilitó.
5. Ejecuta `/vo ayuda`.

La primera ejecución crea `plugins/VytalOptimizer/` con sus archivos de configuración.

## Comandos

Todos los comandos aceptan el nombre completo `/vytaloptimizer` o el alias corto `/vo`.

| Comando | Función |
|---|---|
| `/vo` | Muestra la ayuda. |
| `/vo ayuda` | Lista los comandos disponibles. |
| `/vo analizar` | Ejecuta un diagnóstico general. |
| `/vo plugins` | Muestra plugins y tareas pendientes por propietario. |
| `/vo tps` | Consulta TPS nativos si están disponibles e intervalo aproximado del monitor. |
| `/vo memoria` | Muestra estadísticas de memoria JVM. |
| `/vo entidades` | Muestra entidades y chunks por mundo y los tipos de entidad más numerosos. |
| `/vo reporte` | Genera el informe general. |

Ejemplo:

```text
/vo analizar
```

## Interpretación de resultados

### TPS e intervalo de ticks
- **TPS:** se muestra si la implementación del servidor expone el dato mediante la API consultada.
- **Intervalo aproximado:** tiempo real entre ejecuciones de la tarea de muestreo de VytalOptimizer.
- **No equivale a MSPT de profiler:** no mide directamente el tiempo completo de procesamiento de cada tick.

### Memoria JVM
- **Heap usado:** memoria del heap utilizada.
- **Heap reservado:** memoria que la JVM tiene reservada.
- **Máximo heap:** límite máximo del heap.
- **No-heap:** memoria JVM fuera del heap.

La API Bukkit/Spigot no permite separar de forma fiable la memoria global por plugin.

### Entidades y chunks
Muestra entidades y chunks cargados por mundo, además de los tipos de entidad más numerosos. Una cantidad alta no demuestra por sí sola que exista lag; debe evaluarse junto con la actividad del servidor.

### Plugins y tareas
Enumera plugins, versiones y estado, y agrupa las tareas pendientes por propietario cuando la API lo permite.

**Muchas tareas pendientes no significan necesariamente que un plugin cause lag.** Para medir consumo real de CPU por plugin o tiempo de ejecución de tareas se necesita instrumentación o un profiler compatible.

## Límites de esta versión

La API Bukkit/Spigot compartida no ofrece una medición universal del consumo exacto de CPU, memoria o tiempo de ejecución por plugin. Por ello, VytalOptimizer:
- No acusa a un plugin basándose solo en el número de tareas.
- No atribuye la memoria global de la JVM a plugins individuales.
- No presenta el intervalo del monitor como MSPT real.
- No modifica configuraciones ni desactiva plugins automáticamente.

Para investigar el origen del lag, complementa el diagnóstico con un profiler compatible con tu versión y plataforma.

## Configuración

Archivos ubicados en:

```text
plugins/VytalOptimizer/
├── config.yml
└── messages.yml
```

Configuración inicial de `config.yml`:

```yaml
settings:
  language: es
  sampling-interval-ticks: 20
  max-task-details: 15
  show-warnings: true

messages:
  prefix: "&8[&bVytalOptimizer&8] &7"
```

- `language`: idioma previsto.
- `sampling-interval-ticks`: intervalo de muestreo en ticks; 20 ticks equivalen a un segundo si el servidor mantiene 20 TPS.
- `max-task-details`: reservado para limitar detalles de tareas en futuras ampliaciones.
- `show-warnings`: opción reservada para avisos configurables.

`messages.yml` contiene textos personalizables con códigos `&`. En esta versión inicial, algunos ajustes están preparados para ampliaciones futuras y todavía no controlan una función visible. Reinicia el servidor después de editar los archivos.

## Permisos

| Permiso | Descripción | Por defecto |
|---|---|---|
| `vytaloptimizer.admin` | Permite utilizar los comandos del plugin. | Operadores (`op`) |

Ejemplo con LuckPerms:

```text
/lp group admin permission set vytaloptimizer.admin true
```

## Redes Velocity

VytalOptimizer es un plugin de **backend**. En una red Velocity, instálalo en cada servidor Spigot, Paper o Purpur que quieras analizar. No se instala en Velocity ni recopila métricas de todos los servidores desde una única instancia.

## Compilar desde el código fuente

Requisitos: JDK 21 y Gradle 9.1.0.

```bash
gradle build
```

El JAR se genera en `build/libs/VytalOptimizer-1.0.0.jar`.

## GitHub Actions

El proyecto incluye `.github/workflows/build.yml`.

1. Sube el proyecto a un repositorio de GitHub.
2. Abre **Actions**.
3. Selecciona **Build VytalOptimizer**.
4. Pulsa **Run workflow**, o realiza un push a `main`/`master`.
5. Cuando finalice, abre la ejecución y descarga el artefacto **VytalOptimizer**.

El workflow configura Java 21, Gradle 9.1.0, ejecuta `gradle build` y publica el JAR como artefacto.

## Solución de problemas

**El comando `/vo` no funciona**
- Comprueba que el JAR esté en `plugins/`.
- Revisa los errores de carga en la consola.
- Verifica la versión de Minecraft y Java.

**No aparecen los TPS nativos**
- Algunas plataformas no exponen el dato mediante la API consultada. Las demás métricas seguirán disponibles.

**Un plugin tiene muchas tareas**
- Es una pista para investigar, no una prueba de que cause lag.
- Usa un profiler para medir el tiempo real de ejecución.

**La memoria parece elevada**
- Revisa heap usado, heap máximo, memoria no-heap y registros de la JVM.
- La memoria total del proceso incluye áreas adicionales a las que muestra el heap.

---

**VytalOptimizer — Advanced Performance Analyzer**  
Herramienta de diagnóstico para ayudar a administradores a comprender el estado y el rendimiento de sus servidores Minecraft.
