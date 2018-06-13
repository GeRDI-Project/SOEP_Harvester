# GeRDI Harvester Image for 'SOEP'

FROM jetty:9.4.7-alpine

COPY \/target\/*.war $JETTY_BASE\/webapps\/soep.war

EXPOSE 8080