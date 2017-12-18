# GeRDI Harvester Image for 'SSP'

FROM jetty:9.4.7-alpine

COPY \/target\/*.war $JETTY_BASE\/webapps\/ssp.war

EXPOSE 8080