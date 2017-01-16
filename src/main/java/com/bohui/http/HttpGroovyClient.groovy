package com.bohui.http

import groovyx.net.http.HTTPBuilder
import net.sf.json.JSON

/**
 *
 * User: liyangli
 * Date: 2015/9/23
 * Time: 10:42
 */
def http = new HTTPBuilder( 'http://172.16.250.99:20151' )

// perform a GET request, expecting JSON response data
http.request( GET, JSON ) {
    url.path = '/ajax/services/search/web'
    url.query = [ v:'1.0', q: 'Calvin and Hobbes' ]

    headers.'User-Agent' = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

    // response handler for a success response code:
    response.success = { resp, json ->
        println resp.statusLine

        // parse the JSON response object:
        json.responseData.results.each {
            println "  ${it.titleNoFormatting} : ${it.visibleUrl}"
        }
    }

    // handler for any failure status code:
    response.failure = { resp ->
        println "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
    }
}