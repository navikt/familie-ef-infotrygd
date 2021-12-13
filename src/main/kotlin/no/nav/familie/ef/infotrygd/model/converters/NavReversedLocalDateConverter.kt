package no.nav.familie.ef.infotrygd.model.converters

import javax.persistence.Converter

@Converter
class NavReversedLocalDateConverter : AbstractNavLocalDateConverter("ddMMyyyy")