@ignore
Feature:

  Background:
    * configure responseHeaders = { 'Content-Type': 'application/json' }
    * configure cors = true

  Scenario: pathMatches('/inventories/lock') && methodIs('get')
    * def response = 'success'

  Scenario: pathMatches('/rest/plat/uam/v1/certificates') && methodIs('get')
    * def response = {}

  Scenario: pathMatches('/controller/campus/v3/device/internal/device') && methodIs('POST')
    * def response = {"data":{}}

  Scenario: pathMatches('/controller/campus/shortid/internal/anonymous/query') && methodIs('POST') && request.keys.length == 1 && request.keys[0] == 'campus_acm_sdwan_policy_shortId'
    * def response = {"errcode": "0", "errmsg": "", "data":[{"shortId":"campus_acm_sdwan_policy_shortId","shortValue":"9"}]}
