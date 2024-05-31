package no.nav.familie.ef.infotrygd.model.converters

import jakarta.persistence.Converter

@Converter
class NavLocalDateConverter : AbstractNavLocalDateConverter("yyyyMMdd")
