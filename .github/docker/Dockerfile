ARG ARCH='amd64'
ARG GRADLE_VERSION='8.6'
ARG JAVA_VERSION='21'

FROM ${ARCH}/gradle:${GRADLE_VERSION}-jdk${JAVA_VERSION}-graal AS build
COPY . .
RUN gradle nativeCompile

FROM ${ARCH}/debian:bookworm
COPY --from=build --chmod=755 /home/gradle/build/native/nativeCompile/sympauthy /
ENTRYPOINT ["/sympauthy"]
