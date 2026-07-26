<?xml version="1.0" encoding="utf-8"?>
<!--

Pretty XML Tree Viewer 1.0 (15 Oct 2001):
An XPath/XSLT visualisation tool for XML documents

Written by Mike J. Brown and Jeni Tennison.
No license; use freely, but please credit the authors if republishing elsewhere.

Use this stylesheet to produce an HTML document containing an ASCII art
representation of an XML document's node tree, as exposed by the XML parser
and interpreted by the XSLT processor. Note that the parser may not expose
comments to the XSLT processor.

Usage notes
===========

The output from this stylesheet is HTML that relies heavily on the tree-view.css
stylesheet. If you need plain text output, use the ASCII-only version, not this
stylesheet.

By default, this stylesheet will not show namespace nodes. If the XSLT processor
supports the namespace axis and you want to see namespace nodes, just pass a
non-empty "show_ns" parameter to the stylesheet. Example using Instant Saxon:

    saxon somefile.xml tree-view.xsl show_ns=yes

If you want to ignore whitespace-only text nodes, uncomment the xsl:strip-space
instruction below. This is recommended if you are a beginner.

-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html" indent="no"/>

<!--
  <xsl:strip-space elements="*"/>
-->

  <xsl:param name="show_ns"/>
  <xsl:variable name="apos">'</xsl:variable>

  <xsl:template match="/">
    <html>
      <head>
        <title>SQL query structure genreate by General SQL Parser at www.sqlparser.com</title>
        <link type="text/css" rel="stylesheet" href="tree-view.css"/>
      </head>
      <body>
        <h3>SQL query structure genreated by General SQL Parser at www.sqlparser.com</h3>
        <xsl:apply-templates select="." mode="render"/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="/" mode="render">
    <span class="root">root</span>
    <br/>
    <xsl:apply-templates mode="render"/>
  </xsl:template>

  <xsl:template match="*" mode="render">
    <xsl:call-template name="ascii-art-hierarchy"/>
    <br/>
    <xsl:call-template name="ascii-art-hierarchy"/>
    <span class='connector'>___</span>
    <span class="element">element</span>
    <xsl:text>&#160;</xsl:text>
    <xsl:if test="namespace-uri()">
      <xsl:text>{</xsl:text>
      <span class="uri">
        <xsl:value-of select="namespace-uri()"/>
      </span>
      <xsl:text>}</xsl:text>
    </xsl:if>
    <span class="name">
      <xsl:value-of select="local-name()"/>
    </span>
    <xsl:if test="local-name() != name()">
      <xsl:text> (QName </xsl:text>
      <span class="altname">
        <xsl:value-of select="name()"/>
      </span>
      <xsl:text>)</xsl:text>
    </xsl:if>
    <br/>
    <xsl:apply-templates select="@*" mode="render"/>
    <xsl:if test="$show_ns">
      <xsl:for-each select="namespace::*">
        <xsl:sort select="name()"/>
        <xsl:call-template name="ascii-art-hierarchy"/>
        <span class='connector'>&#160;&#160;</span>
        <span class='connector'>\___</span>
        <span class="namespace">namespace</span>
        <xsl:text>&#160;</xsl:text>
        <xsl:choose>
          <xsl:when test="name()">
            <span class="name">
              <xsl:value-of select="name()"/>
            </span>
          </xsl:when>
          <xsl:otherwise>#default</xsl:otherwise>
        </xsl:choose>
        <xsl:text> = </xsl:text>
        <span class="uri">
          <xsl:value-of select="."/>
        </span>
        <br/>
      </xsl:for-each>
    </xsl:if>
    <xsl:apply-templates mode="render"/>
  </xsl:template>

  <xsl:template match="@*" mode="render">
    <xsl:call-template name="ascii-art-hierarchy"/>
    <span class='connector'>&#160;&#160;</span>
    <span class='connector'>\___</span>
    <span class="attribute">attribute</span>
    <xsl:text>&#160;</xsl:text>
    <xsl:if test="namespace-uri()">
      <xsl:text>{</xsl:text>
      <span class="uri">
        <xsl:value-of select="namespace-uri()"/>
      </span>
      <xsl:text>}</xsl:text>
    </xsl:if>
    <span class="name">
      <xsl:value-of select="local-name()"/>
    </span>
    <xsl:if test="local-name() != name()">
      <xsl:text> (QName </xsl:text>
      <span class="altname">
        <xsl:value-of select="name()"/>
      </span>
      <xsl:text>)</xsl:text>
    </xsl:if>
    <xsl:text> = </xsl:text>
    <span class="value">
      <!-- make spaces be non-breaking spaces, since this is HTML -->
      <xsl:call-template name="escape-ws">
        <xsl:with-param name="text" select="translate(.,' ','&#160;')"/>
      </xsl:call-template>
    </span>
    <br/>
  </xsl:template>

  <xsl:template match="text()" mode="render">
    <xsl:call-template name="ascii-art-hierarchy"/>
    <br/>
    <xsl:call-template name="ascii-art-hierarchy"/>
    <span class='connector'>___</span>
    <span class="text">text</span>
    <xsl:text>&#160;</xsl:text>
    <span class="value">
      <!-- make spaces be non-breaking spaces, since this is HTML -->
      <xsl:call-template name="escape-ws">
        <xsl:with-param name="text" select="translate(.,' ','&#160;')"/>
      </xsl:call-template>
    </span>
    <br/>
  </xsl:template>

  <xsl:template match="comment()" mode="render">
    <xsl:call-template name="ascii-art-hierarchy"/>
    <br/>
    <xsl:call-template name="ascii-art-hierarchy"/>
    <span class='connector'>___</span>
    <span class="comment">comment</span>
    <xsl:text>&#160;</xsl:text>
    <span class="value">
      <!-- make spaces be non-breaking spaces, since this is HTML -->
      <xsl:call-template name="escape-ws">
        <xsl:with-param name="text" select="translate(.,' ','&#160;')"/>
      </xsl:call-template>
    </span>
    <br/>
  </xsl:template>

  <xsl:template match="processing-instruction()" mode="render">
    <xsl:call-template name="ascii-art-hierarchy"/>
    <br/>
    <xsl:call-template name="ascii-art-hierarchy"/>
    <span class='connector'>___</span>
    <span class="pi">processing instruction</span>
    <xsl:text>&#160;</xsl:text>
    <xsl:text>target=</xsl:text>
    <span class="value">
      <xsl:value-of select="name()"/>
    </span>
    <xsl:text>&#160;instruction=</xsl:text>
    <span class="value">
      <xsl:value-of select="."/>
    </span>
    <br/>
  </xsl:template>

  <xsl:template name="ascii-art-hierarchy">
    <xsl:for-each select="ancestor::*">
      <xsl:choose>
        <xsl:when test="following-sibling::node()">
          <span class='connector'>&#160;&#160;</span>|<span class='connector'>&#160;&#160;</span>
          <xsl:text>&#160;</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <span class='connector'>&#160;&#160;&#160;&#160;</span>
          <span class='connector'>&#160;&#160;</span>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
    <xsl:choose>
      <xsl:when test="parent::node() and ../child::node()">
        <span class='connector'>&#160;&#160;</span>
        <xsl:text>|</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <span class='connector'>&#160;&#160;&#160;</span>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- recursive template to escape linefeeds, tabs -->
  <xsl:template name="escape-ws">
    <xsl:param name="text"/>
    <xsl:choose>
      <xsl:when test="contains($text, '&#xA;')">
        <xsl:call-template name="escape-ws">
          <xsl:with-param name="text" select="substring-before($text, '&#xA;')"/>
        </xsl:call-template>
        <span class="escape">\n</span>
        <xsl:call-template name="escape-ws">
          <xsl:with-param name="text" select="substring-after($text, '&#xA;')"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="contains($text, '&#x9;')">
        <xsl:value-of select="substring-before($text, '&#x9;')"/>
        <span class="escape">\t</span>
        <xsl:call-template name="escape-ws">
          <xsl:with-param name="text" select="substring-after($text, '&#x9;')"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise><xsl:value-of select="$text"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
