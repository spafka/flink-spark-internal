@REM marathon launcher script
@REM
@REM Environment:
@REM JAVA_HOME - location of a JDK home dir (optional if java on path)
@REM CFG_OPTS  - JVM options (optional)
@REM Configuration:
@REM MARATHON_config.txt found in the MARATHON_HOME.
@setlocal enabledelayedexpansion

@echo off

if "%MARATHON_HOME%"=="" set "MARATHON_HOME=%~dp0\\.."

set "APP_LIB_DIR=%MARATHON_HOME%\lib\"

rem Detect if we were double clicked, although theoretically A user could
rem manually run cmd /c
for %%x in (!cmdcmdline!) do if %%~x==/c set DOUBLECLICKED=1

rem FIRST we load the config file of extra options.
set "CFG_FILE=%MARATHON_HOME%\MARATHON_config.txt"
set CFG_OPTS=
if exist "%CFG_FILE%" (
  FOR /F "tokens=* eol=# usebackq delims=" %%i IN ("%CFG_FILE%") DO (
    set DO_NOT_REUSE_ME=%%i
    rem ZOMG (Part #2) WE use !! here to delay the expansion of
    rem CFG_OPTS, otherwise it remains "" for this loop.
    set CFG_OPTS=!CFG_OPTS! !DO_NOT_REUSE_ME!
  )
)

rem We use the value of the JAVACMD environment variable if defined
set _JAVACMD=%JAVACMD%

if "%_JAVACMD%"=="" (
  if not "%JAVA_HOME%"=="" (
    if exist "%JAVA_HOME%\bin\java.exe" set "_JAVACMD=%JAVA_HOME%\bin\java.exe"
  )
)

if "%_JAVACMD%"=="" set _JAVACMD=java

rem Detect if this java is ok to use.
for /F %%j in ('"%_JAVACMD%" -version  2^>^&1') do (
  if %%~j==java set JAVAINSTALLED=1
  if %%~j==openjdk set JAVAINSTALLED=1
)

rem BAT has no logical or, so we do it OLD SCHOOL! Oppan Redmond Style
set JAVAOK=true
if not defined JAVAINSTALLED set JAVAOK=false

if "%JAVAOK%"=="false" (
  echo.
  echo A Java JDK is not installed or can't be found.
  if not "%JAVA_HOME%"=="" (
    echo JAVA_HOME = "%JAVA_HOME%"
  )
  echo.
  echo Please go to
  echo   http://www.oracle.com/technetwork/java/javase/downloads/index.html
  echo and download a valid Java JDK and install before running marathon.
  echo.
  echo If you think this message is in error, please check
  echo your environment variables to see if "java.exe" and "javac.exe" are
  echo available via JAVA_HOME or PATH.
  echo.
  if defined DOUBLECLICKED pause
  exit /B 1
)


rem We use the value of the JAVA_OPTS environment variable if defined, rather than the config.
set _JAVA_OPTS=%JAVA_OPTS%
if "!_JAVA_OPTS!"=="" set _JAVA_OPTS=!CFG_OPTS!

rem We keep in _JAVA_PARAMS all -J-prefixed and -D-prefixed arguments
rem "-J" is stripped, "-D" is left as is, and everything is appended to JAVA_OPTS
set _JAVA_PARAMS=
set _APP_ARGS=

:param_loop
call set _PARAM1=%%1
set "_TEST_PARAM=%~1"

if ["!_PARAM1!"]==[""] goto param_afterloop


rem ignore arguments that do not start with '-'
if "%_TEST_PARAM:~0,1%"=="-" goto param_java_check
set _APP_ARGS=!_APP_ARGS! !_PARAM1!
shift
goto param_loop

:param_java_check
if "!_TEST_PARAM:~0,2!"=="-J" (
  rem strip -J prefix
  set _JAVA_PARAMS=!_JAVA_PARAMS! !_TEST_PARAM:~2!
  shift
  goto param_loop
)

if "!_TEST_PARAM:~0,2!"=="-D" (
  rem test if this was double-quoted property "-Dprop=42"
  for /F "delims== tokens=1,*" %%G in ("!_TEST_PARAM!") DO (
    if not ["%%H"] == [""] (
      set _JAVA_PARAMS=!_JAVA_PARAMS! !_PARAM1!
    ) else if [%2] neq [] (
      rem it was a normal property: -Dprop=42 or -Drop="42"
      call set _PARAM1=%%1=%%2
      set _JAVA_PARAMS=!_JAVA_PARAMS! !_PARAM1!
      shift
    )
  )
) else (
  if "!_TEST_PARAM!"=="-main" (
    call set CUSTOM_MAIN_CLASS=%%2
    shift
  ) else (
    set _APP_ARGS=!_APP_ARGS! !_PARAM1!
  )
)
shift
goto param_loop
:param_afterloop

set _JAVA_OPTS=!_JAVA_OPTS! !_JAVA_PARAMS!
:run

set "APP_CLASSPATH=%APP_LIB_DIR%\mesosphere.marathon.marathon-1.5.0-96-gf84298d.jar;%APP_LIB_DIR%\mesosphere.marathon.plugin-interface-1.5.0-96-gf84298d.jar;%APP_LIB_DIR%\org.apache.curator.curator-client-2.11.1.jar;%APP_LIB_DIR%\com.thoughtworks.paranamer.paranamer-2.8.jar;%APP_LIB_DIR%\org.slf4j.slf4j-api-1.7.24.jar;%APP_LIB_DIR%\com.google.protobuf.protobuf-java-3.3.0.jar;%APP_LIB_DIR%\net.logstash.logback.logstash-logback-encoder-4.9.jar;%APP_LIB_DIR%\org.mozilla.rhino-1.7.7.jar;%APP_LIB_DIR%\com.fasterxml.jackson.module.jackson-module-scala_2.11-2.7.2.jar;%APP_LIB_DIR%\io.netty.netty-handler-4.0.43.Final.jar;%APP_LIB_DIR%\com.fasterxml.jackson.core.jackson-annotations-2.7.8.jar;%APP_LIB_DIR%\com.google.inject.guice-3.0.jar;%APP_LIB_DIR%\com.getsentry.raven.raven-7.8.6.jar;%APP_LIB_DIR%\io.kamon.kamon-system-metrics_2.11-0.6.7.jar;%APP_LIB_DIR%\org.scala-lang.modules.scala-java8-compat_2.11-0.8.0.jar;%APP_LIB_DIR%\com.fasterxml.jackson.jaxrs.jackson-jaxrs-json-provider-2.7.2.jar;%APP_LIB_DIR%\joda-time.joda-time-2.9.9.jar;%APP_LIB_DIR%\com.typesafe.akka.akka-actor_2.11-2.4.18.jar;%APP_LIB_DIR%\com.sun.jersey.jersey-server-1.18.6.jar;%APP_LIB_DIR%\com.fasterxml.jackson.datatype.jackson-datatype-jdk8-2.7.8.jar;%APP_LIB_DIR%\com.typesafe.netty.netty-reactive-streams-1.0.8.jar;%APP_LIB_DIR%\com.fasterxml.jackson.dataformat.jackson-dataformat-cbor-2.6.6.jar;%APP_LIB_DIR%\io.dropwizard.metrics.metrics-annotation-3.1.2.jar;%APP_LIB_DIR%\org.scala-lang.modules.scala-parser-combinators_2.11-1.0.4.jar;%APP_LIB_DIR%\io.netty.netty-common-4.0.43.Final.jar;%APP_LIB_DIR%\aopalliance.aopalliance-1.0.jar;%APP_LIB_DIR%\net.sf.jopt-simple.jopt-simple-4.6.jar;%APP_LIB_DIR%\com.typesafe.play.play-json_2.11-2.5.14.jar;%APP_LIB_DIR%\io.dropwizard.metrics.metrics-json-3.1.2.jar;%APP_LIB_DIR%\io.kamon.kamon-statsd_2.11-0.6.7.jar;%APP_LIB_DIR%\javax.inject.javax.inject-1.jar;%APP_LIB_DIR%\com.github.fge.json-schema-validator-2.2.6.jar;%APP_LIB_DIR%\org.aspectj.aspectjrt-1.8.10.jar;%APP_LIB_DIR%\com.sksamuel.scapegoat.scalac-scapegoat-plugin_2.11-1.3.0.jar;%APP_LIB_DIR%\org.apache.commons.commons-compress-1.13.jar;%APP_LIB_DIR%\com.typesafe.akka.akka-parsing_2.11-10.0.6.jar;%APP_LIB_DIR%\com.github.fge.uri-template-0.9.jar;%APP_LIB_DIR%\org.asynchttpclient.async-http-client-2.0.25.jar;%APP_LIB_DIR%\org.scala-stm.scala-stm_2.11-0.7.jar;%APP_LIB_DIR%\commons-io.commons-io-2.5.jar;%APP_LIB_DIR%\org.apache.curator.curator-recipes-2.11.1.jar;%APP_LIB_DIR%\commons-beanutils.commons-beanutils-1.9.3.jar;%APP_LIB_DIR%\com.wix.accord-api_2.11-0.5.jar;%APP_LIB_DIR%\io.dropwizard.metrics.metrics-servlets-3.1.2.jar;%APP_LIB_DIR%\org.apache.zookeeper.zookeeper-3.4.8.jar;%APP_LIB_DIR%\com.typesafe.ssl-config-core_2.11-0.2.1.jar;%APP_LIB_DIR%\javax.activation.activation-1.1.jar;%APP_LIB_DIR%\org.asynchttpclient.netty-codec-dns-2.0.25.jar;%APP_LIB_DIR%\org.scala-lang.scala-library-2.11.11.jar;%APP_LIB_DIR%\com.github.fge.json-schema-core-1.2.5.jar;%APP_LIB_DIR%\com.wix.accord-core_2.11-0.5.jar;%APP_LIB_DIR%\org.slf4j.jcl-over-slf4j-1.7.21.jar;%APP_LIB_DIR%\ch.qos.logback.logback-classic-1.2.1.jar;%APP_LIB_DIR%\mesosphere.chaos_2.11-0.8.8.jar;%APP_LIB_DIR%\com.github.spullara.mustache.java.compiler-0.9.0.jar;%APP_LIB_DIR%\org.eclipse.jetty.jetty-io-9.3.6.v20151106.jar;%APP_LIB_DIR%\io.netty.netty-3.7.0.Final.jar;%APP_LIB_DIR%\mesosphere.marathon.ui-1.2.0.jar;%APP_LIB_DIR%\software.amazon.ion.ion-java-1.0.2.jar;%APP_LIB_DIR%\com.typesafe.play.play-iteratees_2.11-2.5.14.jar;%APP_LIB_DIR%\com.typesafe.akka.akka-slf4j_2.11-2.4.18.jar;%APP_LIB_DIR%\org.apache.httpcomponents.httpclient-4.5.2.jar;%APP_LIB_DIR%\com.typesafe.akka.akka-http_2.11-10.0.6.jar;%APP_LIB_DIR%\javax.mail.mailapi-1.4.3.jar;%APP_LIB_DIR%\org.rogach.scallop_2.11-1.0.0.jar;%APP_LIB_DIR%\io.kamon.kamon-jmx_2.11-0.6.7.jar;%APP_LIB_DIR%\com.fasterxml.jackson.datatype.jackson-datatype-jsr310-2.7.8.jar;%APP_LIB_DIR%\com.google.code.findbugs.jsr305-3.0.0.jar;%APP_LIB_DIR%\org.apache.httpcomponents.httpcore-4.4.4.jar;%APP_LIB_DIR%\org.joda.joda-convert-1.8.1.jar;%APP_LIB_DIR%\com.github.fge.jackson-coreutils-1.8.jar;%APP_LIB_DIR%\io.dropwizard.metrics.metrics-healthchecks-3.1.2.jar;%APP_LIB_DIR%\org.apache.curator.curator-framework-2.11.1.jar;%APP_LIB_DIR%\org.asynchttpclient.netty-resolver-dns-2.0.25.jar;%APP_LIB_DIR%\org.slf4j.log4j-over-slf4j-1.7.21.jar;%APP_LIB_DIR%\org.aspectj.aspectjweaver-1.8.10.jar;%APP_LIB_DIR%\com.sun.jersey.contribs.jersey-guice-1.18.1.jar;%APP_LIB_DIR%\javax.validation.validation-api-1.1.0.Final.jar;%APP_LIB_DIR%\de.heikoseeberger.akka-http-play-json_2.11-1.10.1.jar;%APP_LIB_DIR%\org.asynchttpclient.async-http-client-netty-utils-2.0.25.jar;%APP_LIB_DIR%\io.dropwizard.metrics.metrics-jersey-3.1.2.jar;%APP_LIB_DIR%\ch.qos.logback.logback-core-1.2.1.jar;%APP_LIB_DIR%\org.eclipse.jetty.jetty-util-9.3.6.v20151106.jar;%APP_LIB_DIR%\org.reactivestreams.reactive-streams-1.0.0.jar;%APP_LIB_DIR%\com.google.guava.guava-19.0.jar;%APP_LIB_DIR%\io.reactivex.rxjava-1.2.4.jar;%APP_LIB_DIR%\org.apache.mesos.mesos-1.4.0-rc3.jar;%APP_LIB_DIR%\com.google.inject.extensions.guice-servlet-3.0.jar;%APP_LIB_DIR%\com.sun.jersey.jersey-servlet-1.18.6.jar;%APP_LIB_DIR%\org.javassist.javassist-3.21.0-GA.jar;%APP_LIB_DIR%\com.fasterxml.uuid.java-uuid-generator-3.1.4.jar;%APP_LIB_DIR%\javax.servlet.javax.servlet-api-3.1.0.jar;%APP_LIB_DIR%\de.heikoseeberger.akka-sse_2.11-2.0.0.jar;%APP_LIB_DIR%\commons-collections.commons-collections-3.2.2.jar;%APP_LIB_DIR%\io.dropwizard.metrics.metrics-core-3.1.2.jar;%APP_LIB_DIR%\io.dropwizard.metrics.metrics-jvm-3.1.2.jar;%APP_LIB_DIR%\org.slf4j.jul-to-slf4j-1.7.21.jar;%APP_LIB_DIR%\commons-codec.commons-codec-1.9.jar;%APP_LIB_DIR%\com.typesafe.play.play-functional_2.11-2.5.14.jar;%APP_LIB_DIR%\io.kamon.sigar-loader-1.6.5-rev002.jar;%APP_LIB_DIR%\com.amazonaws.aws-java-sdk-core-1.11.129.jar;%APP_LIB_DIR%\io.netty.netty-buffer-4.0.43.Final.jar;%APP_LIB_DIR%\org.hibernate.hibernate-validator-5.2.1.Final.jar;%APP_LIB_DIR%\com.fasterxml.jackson.core.jackson-databind-2.7.8.jar;%APP_LIB_DIR%\io.netty.netty-codec-http-4.0.43.Final.jar;%APP_LIB_DIR%\org.scala-lang.modules.scala-async_2.11-0.9.6.jar;%APP_LIB_DIR%\org.javabits.jgrapht.jgrapht-core-0.9.3.jar;%APP_LIB_DIR%\com.typesafe.config-1.3.1.jar;%APP_LIB_DIR%\com.typesafe.scala-logging.scala-logging_2.11-3.5.0.jar;%APP_LIB_DIR%\com.typesafe.akka.akka-http-core_2.11-10.0.6.jar;%APP_LIB_DIR%\org.eclipse.jetty.jetty-http-9.3.6.v20151106.jar;%APP_LIB_DIR%\com.github.fge.msg-simple-1.1.jar;%APP_LIB_DIR%\com.googlecode.libphonenumber.libphonenumber-6.2.jar;%APP_LIB_DIR%\org.hdrhistogram.HdrHistogram-2.1.9.jar;%APP_LIB_DIR%\mesosphere.marathon.api-console-3.0.8-accept.jar;%APP_LIB_DIR%\org.eclipse.jetty.jetty-server-9.3.6.v20151106.jar;%APP_LIB_DIR%\jline.jline-0.9.94.jar;%APP_LIB_DIR%\com.fasterxml.jackson.core.jackson-core-2.8.7.jar;%APP_LIB_DIR%\com.getsentry.raven.raven-logback-7.8.6.jar;%APP_LIB_DIR%\io.reactivex.rxscala_2.11-0.26.5.jar;%APP_LIB_DIR%\com.fasterxml.jackson.module.jackson-module-paranamer-2.7.2.jar;%APP_LIB_DIR%\org.scala-lang.modules.scala-xml_2.11-1.0.5.jar;%APP_LIB_DIR%\net.liftweb.lift-markdown_2.11-2.6.2.jar;%APP_LIB_DIR%\org.scalamacros.resetallattrs_2.11-1.0.0.jar;%APP_LIB_DIR%\org.asynchttpclient.netty-resolver-2.0.25.jar;%APP_LIB_DIR%\com.typesafe.akka.akka-stream_2.11-2.4.18.jar;%APP_LIB_DIR%\com.github.fge.btf-1.2.jar;%APP_LIB_DIR%\com.typesafe.play.play-datacommons_2.11-2.5.14.jar;%APP_LIB_DIR%\org.jvnet.mimepull.mimepull-1.9.3.jar;%APP_LIB_DIR%\com.sun.jersey.jersey-core-1.18.6.jar;%APP_LIB_DIR%\org.jboss.logging.jboss-logging-3.2.1.Final.jar;%APP_LIB_DIR%\org.eclipse.jetty.jetty-servlets-9.3.6.v20151106.jar;%APP_LIB_DIR%\org.eclipse.jetty.jetty-security-9.3.6.v20151106.jar;%APP_LIB_DIR%\com.sun.jersey.contribs.jersey-multipart-1.18.6.jar;%APP_LIB_DIR%\io.kamon.kamon-core_2.11-0.6.7.jar;%APP_LIB_DIR%\com.fasterxml.jackson.module.jackson-module-jaxb-annotations-2.7.2.jar;%APP_LIB_DIR%\io.kamon.kamon-datadog_2.11-0.6.7.jar;%APP_LIB_DIR%\com.typesafe.akka.akka-http-xml_2.11-10.0.5.jar;%APP_LIB_DIR%\org.scala-lang.scala-reflect-2.11.11.jar;%APP_LIB_DIR%\io.netty.netty-transport-4.0.43.Final.jar;%APP_LIB_DIR%\com.fasterxml.jackson.jaxrs.jackson-jaxrs-base-2.7.2.jar;%APP_LIB_DIR%\io.dropwizard.metrics.metrics-jetty9-3.1.2.jar;%APP_LIB_DIR%\io.kamon.kamon-scala_2.11-0.6.7.jar;%APP_LIB_DIR%\org.eclipse.jetty.jetty-continuation-9.3.6.v20151106.jar;%APP_LIB_DIR%\com.fasterxml.classmate-1.1.0.jar;%APP_LIB_DIR%\org.eclipse.jetty.jetty-servlet-9.3.6.v20151106.jar;%APP_LIB_DIR%\io.netty.netty-codec-4.0.43.Final.jar;%APP_LIB_DIR%\io.kamon.kamon-autoweave_2.11-0.6.5.jar;%APP_LIB_DIR%\com.lightbend.akka.akka-stream-alpakka-s3_2.11-0.8.jar"
set "APP_MAIN_CLASS=mesosphere.marathon.Main"

if defined CUSTOM_MAIN_CLASS (
    set MAIN_CLASS=!CUSTOM_MAIN_CLASS!
) else (
    set MAIN_CLASS=!APP_MAIN_CLASS!
)

rem Call the application and pass all arguments unchanged.
"%_JAVACMD%" !_JAVA_OPTS! !MARATHON_OPTS! -cp "%APP_CLASSPATH%" %MAIN_CLASS% !_APP_ARGS!

@endlocal


:end

exit /B %ERRORLEVEL%
