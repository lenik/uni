<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:apply-templates select="/searchNGResponse/data/artifact"/>
	</xsl:template>
	<xsl:template match="/searchNGResponse/data/artifact">
		<xsl:value-of select="concat(groupId, ':', artifactId, ':', version)"/>
		<xsl:if test="artifactHits/artifactHit/artifactLinks/artifactLink"> (<xsl:for-each select="artifactHits/artifactHit/artifactLinks/artifactLink">
				<xsl:if test="position() != 1">, </xsl:if>
				<xsl:choose>
					<xsl:when test="classifier">
						<xsl:value-of select="concat(classifier, '.', extension)"/>
					</xsl:when>
					<xsl:when test="extension">
						<xsl:value-of select="extension"/>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>)</xsl:if>
		<xsl:value-of select="'&#10;'"/>
	</xsl:template>
</xsl:stylesheet>
