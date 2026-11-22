# VytalOptimizer

Plugin de diagnóstico de rendimiento para **Spigot, Paper y Purpur**, desde Minecraft 1.20 en adelante.

## Requisitos
- Java 21 para compilar.
- Servidor Minecraft 1.20+.
- Gradle 9.1.0 (GitHub Actions lo configura automáticamente).

El código utiliza la API Bukkit/Spigot compartida. Se compila contra Spigot API 1.20.4 y se limita a bytecode Java 17 para mantener compatibilidad con servidores 1.20.x y posteriores que ejecuten Java 21 o superior.

## Comandos
- `/vytaloptimizer` o `/vo`: ayuda.
- `/vo analizar`: diagnóstico general.
- `/vo plugins`: plugins instalados y tareas pendientes agrupadas por propietario.
- `/vo tps`: TPS (si la implementación los expone) y estimación del intervalo entre ticks.
- `/vo memoria`: memoria de la JVM.
- `/vo entidades`: entidades y chunks cargados por mundo.
- `/vo reporte`: resumen completo en consola.
- `/vo ayuda`: ayuda.

Permiso: `vytaloptimizer.admin` (por defecto, operadores).

## Instalación
1. Descarga `VytalOptimizer-1.0.0.jar` desde el artefacto de GitHub Actions.
2. Colócalo en `plugins/`.
3. Reinicia el servidor.
4. Ejecuta `/vo analizar`.

## Qué mide y qué no
VytalOptimizer recoge métricas disponibles desde Bukkit/Spigot: memoria de la JVM, entidades, chunks, mundos, plugins, tareas pendientes del scheduler y tiempos aproximados entre ticks. También intenta leer TPS y tiempo medio de tick mediante reflexión cuando la plataforma los expone.

**Importante:** Bukkit/Spigot no ofrece una API universal para medir el consumo exacto de CPU/RAM de cada plugin ni el tiempo de ejecución individual de cada tarea. Por eso, la sección de tareas muestra cantidad y propietario de tareas pendientes, no afirma que ese plugin sea el causante del lag. Para atribución precisa de CPU por plugin se necesita un profiler/instrumentación compatible. Las recomendaciones son diagnósticas y no modifican ni desactivan plugins.

## Compilar localmente
```bash
gradle build
```
El JAR se genera en `build/libs/`.

## GitHub Actions
El workflow `.github/workflows/build.yml` compila al hacer push a `main`/`master` o manualmente desde Actions y publica el JAR como artefacto.
