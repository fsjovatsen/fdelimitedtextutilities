<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : InputTransformSS.xsl
    Created on : 13. januar 2009, 12:28
    Author     : FSjovatsen
    Description: Convert the records to events based on field[@name='Event']
-->

<xsl:stylesheet extension-element-prefixes="nxsl" version="1.0" xmlns:nxsl="http://www.novell.com/nxsl" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- each application must fill in the name of the field that provides the association key -->
    <xsl:variable name="association-field-name" select="'norEduPersonNIN'"/>

    <!-- each application must fill in the name of the class that the delimited text represents -->
    <xsl:variable name="object-class" select="'User'"/>

    <!-- Symbols/strings that represent ADD, MODIFY and DELETE events in the input document. -->
    <xsl:variable name="add-event" select="'ADD'"/>
    <xsl:variable name="modify-event" select="'MODIFY'"/>
    <xsl:variable name="delete-event" select="'DELETE'"/>

    <xsl:variable name="uc" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />
    <xsl:variable name="lc" select="'abcdefghijklmnopqrstuvwxyz'" />

    <xsl:template match="/">

        <nds dtdversion="1.1" ndsversion="8.6" xml:space="default">
            <input>
				<!-- for each record, do ... -->
                <xsl:for-each select="delimited-text/record">

                    <xsl:variable name="association" select="field[@name=$association-field-name]"/>
                    <xsl:variable name="srcdn" select="concat('RFK-', field[@name=$association-field-name])"/>
                    <xsl:variable name="event" select="field[@name='Event']"/>

                    <xsl:choose>

                        <!-- ADD event -->
                        <xsl:when test="translate($event, $lc, $uc) = $add-event">
                            <xsl:message>DEBUG (InputTransformSS) ==> This is a ADD event</xsl:message>
								<!-- generate the add event -->
                            <add class-name="{$object-class}" src-dn="{$srcdn}">
									<!-- generate the association -->
                                <association>
                                    <xsl:value-of select="$association"/>
                                </association>
									<!-- handle each field -->
                                <xsl:for-each select="field[string()]">
                                    <xsl:variable name="fieldValue" select="normalize-space(.)"/>
										<!-- generate the add-attr -->
                                    <add-attr attr-name="{@name}">
                                        <value type="string">
                                            <xsl:value-of select="$fieldValue"/>
                                        </value>
                                    </add-attr>
                                </xsl:for-each>
                            </add>
                        </xsl:when>
                        <!-- End ADD event -->

                        <!-- MODIFY event -->
                        <xsl:when test="translate($event, $lc, $uc) = $modify-event">
                            <xsl:message>DEBUG (InputTransformSS) ==> This is a MODIFY event</xsl:message>
                            <modify class-name="{$object-class}" src-dn="{$srcdn}">
                                <association>
                                    <xsl:value-of select="$association"/>
                                </association>
                                <xsl:for-each select="field[string()]">
                                    <xsl:variable name="fieldValue" select="normalize-space(.)"/>
										<!-- generate the add-attr -->
                                    <modify-attr attr-name="{@name}">
                                        <remove-all-values/>
                                        <add-value>
                                            <value type="string">
                                                <xsl:value-of select="$fieldValue"/>
                                            </value>
                                        </add-value>
                                    </modify-attr>
                                </xsl:for-each>
                            </modify>
                        </xsl:when>
                        <!-- End MODIFY event -->

                        <!-- DELETE event -->
                        <xsl:when test="translate($event, $lc, $uc) = $delete-event">
                            <xsl:message>DEBUG (InputTransformSS) ==> This is a DELETE event</xsl:message>
                            <delete class-name="{$object-class}" src-dn="{$srcdn}">
                                <association>
                                    <xsl:value-of select="$association"/>
                                </association>
                            </delete>
                        </xsl:when>
                        <!-- End DELETE event -->

                        <!-- If the event don't match ADD, DELETE, MODIFY we
                             return a empty document and sends a message to
                             the trace.
                        -->
                        <xsl:otherwise>
                            <xsl:message>
									DEBUG (InputTransformSS) ==> Unrecognized event.
                            </xsl:message>
                        </xsl:otherwise>

                    </xsl:choose>

                </xsl:for-each>

            </input>
        </nds>

    </xsl:template>
</xsl:stylesheet>
