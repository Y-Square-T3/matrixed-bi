FROM openjdk:8-jre-slim

LABEL "author"="shelton"

# Set to your own TZ instead
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create dedicated user for security
RUN groupadd -r matrixed_bi && useradd -r -g matrixed_bi matrixed_bi

# Create application directory with proper ownership
RUN mkdir -p /matrixed-bi && chown -R matrixed_bi:matrixed_bi /matrixed-bi
WORKDIR /matrixed-bi

# Copy application files
COPY --chown=matrixed_bi:matrixed_bi ./bin/ ./bin/
COPY --chown=matrixed_bi:matrixed_bi ./config/ ./config/
COPY --chown=matrixed_bi:matrixed_bi ./lib/ ./lib/
COPY --chown=matrixed_bi:matrixed_bi static ./static

# Set proper permissions
RUN chmod +x ./bin/* 2>/dev/null || true

# Expose port
EXPOSE 8080

# Run as non-root user
USER matrixed_bi

# Optimize JVM settings and run application
ENTRYPOINT ["java", \
    "-server", \
    "-Xms2G", \
    "-Xmx2G", \
    "-Dspring.profiles.active=config", \
    "-Dfile.encoding=UTF-8", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-cp", "lib/*", \
    "datart.DatartServerApplication"]