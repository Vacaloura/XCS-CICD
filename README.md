# Jakarta EE Calculator

MODIFICACION PARA PROBAR QUE CI DETECTA LOS CAMBIOS CUANDO SE REALIZA EL COMMIT A LA RAMA

MODIFICACION 2 PARA SACAR UNA CAPTURA

Calculadora mínima para una presentación de CI/CD.

- Usa un EJB sin estado para las operaciones,
- un bean CDI `@ViewScoped` para mantener el estado de cada vista
- y Jakarta Faces (JSF) para la interfaz.

## Requisitos

- JDK 21 y `JAVA_HOME` apuntando a ese JDK.
- Maven

## Compilar y probar

Desde la raíz del proyecto, en PowerShell:

```powershell
.\mvnw.cmd --batch-mode clean verify
```

En Linux/macOS usar `sh ./mvnw --batch-mode clean verify`.

El resultado es `target/calculator.war` y los informes de las 20 pruebas unitarias están en `target/surefire-reports/`. Las pruebas aritméticas no necesitan servidor. Las APIs de Jakarta tienen alcance `provided`: las implementaciones las aporta WildFly.

Si el entorno tiene un directorio de usuario sin permisos de escritura, se puede mantener la caché dentro del proyecto (ignorada por Git):

```powershell
$env:MAVEN_USER_HOME = Join-Path (Get-Location) '.cache/maven'
.\mvnw.cmd --batch-mode '-Dmaven.repo.local=.cache/repository' clean verify
```

## Ejecutar localmente

Descomprimir WildFly fuera de los fuentes. Definir `WILDFLY_HOME` con la ruta de instalación. En una terminal:

```powershell
& "$env:WILDFLY_HOME\bin\standalone.bat" -b 127.0.0.1 -bmanagement 127.0.0.1
```

Desde otra terminal, en la raíz del proyecto:

```powershell
& "$env:WILDFLY_HOME\bin\jboss-cli.bat" --connect '--command=deploy target/calculator.war --force'
```

Abrir [la calculadora local](http://localhost:8080/calculator/). Para detener el servidor usar Ctrl+C en su terminal. En Linux/macOS los scripts equivalentes terminan en `.sh`.

## Comportamiento

- Pulsar primer número, operación, segundo número y `=`. No se interpretan expresiones ni prioridades. Antes de otra operación pendiente se pide pulsar `=`.
- `±` cambia el signo y `.` introduce decimales. `C` restaura el estado inicial.
- Cada número introducido admite hasta 16 dígitos. Las operaciones usan `BigDecimal`, redondean a un máximo de 10 decimales con `HALF_UP` y eliminan ceros finales.
- Se puede reutilizar un resultado seleccionando otra operación, o empezar un cálculo nuevo pulsando un dígito.
- Dividir entre cero muestra un mensaje y permite introducir otro divisor o pulsar `C`.
- El estado pertenece a la vista JSF de cada navegador; no se comparte entre usuarios. La sesión caduca tras 15 minutos de inactividad. Si caduca, volver a abrir la página.
