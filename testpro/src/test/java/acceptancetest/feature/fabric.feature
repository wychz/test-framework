Feature: Fabric

  Background:
    Given url baseUrl

  Scenario: create vasPool
    Given path '/acdcn/ui/vas/vaspool'
    * def req =
    """
      {
        "id": "7e3c069f-29ba-47d8-b287-5d69c3a73a99",
        "name": "xxxxx",
        "ac-system-name": "AC111",
        "description": "",
        "internal-interface-mode": "null",
        "external-interface-mode": "null",
        "res-allocated-policy": "random",
        "networking-mode": "in-linedeployment",
        "device-type": "unmanagedfw",
        "device-quota": [ ]
      }
    """
    And request req
    And header Accept = 'application/json'
    When method POST
    Then status 201
    And print response

  Scenario: update vasPool
    Given path '/acdcn/ui/vas/vaspool/7e3c069f-29ba-47d8-b287-5d69c3a73a99'
    * def req =
    """
      {
        "name": "mmmmmm",
        "description": "",
        "internal-interface-mode": "null",
        "external-interface-mode": "null",
        "res-allocated-policy": "random",
        "networking-mode": "in-linedeployment",
        "device-type": "unmanagedfw",
        "device-quota": [ ]
      }
    """
    And request req
    And header Accept = 'application/json'
    When method PUT
    Then status 200
    And print response

  Scenario: delete vasPool by vasPoolId
    Given path '/acdcn/ui/vas/vaspool/7e3c069f-29ba-47d8-b287-5d69c3a73a99'
    And header Accept = 'application/json'
    When method DELETE
    Then status 204
    And print response

  Scenario: create vasPool
    Given path '/acdcn/ui/vas/vaspool'
    * def req =
    """
      {
        "id": "7e3c069f-29ba-47d8-b287-5d69c3a73a99",
        "name": "xxxxx",
        "ac-system-name": "AC111",
        "description": "",
        "internal-interface-mode": "null",
        "external-interface-mode": "null",
        "res-allocated-policy": "random",
        "networking-mode": "in-linedeployment",
        "device-type": "unmanagedfw",
        "device-quota": [ ]
      }
    """
    And request req
    And header Accept = 'application/json'
    When method POST
    Then status 201
    And print response

  Scenario: query vasPool by condition
    Given path '/acdcn/ui/vas/vaspool/name=xxxxxx'
    And header Accept = 'application/json'
    When method GET
    Then status 200
    And print response

  Scenario: query all vasPool
    Given path '/acdcn/ui/vas/vaspool'
    And header Accept = 'application/json'
    When method GET
    Then status 200
    And print response

  Scenario: query vasPool's total by conditon
    Given path '/acdcn/ui/vas/vaspool/queryTotalVasPools/name=xxxxxx'
    And header Accept = 'application/json'
    When method GET
    Then status 200
    And print response

  Scenario: query vasPools's total
    Given path '/acdcn/ui/vas/vaspool/queryTotalVasPools'
    And header Accept = 'application/json'
    When method GET
    Then status 200
    And print response

#  Scenario: add node to vasPool
#    Given path '/acdcn/ui/vas/vaspool/addNodeToVasPool'
#    * def req =
#    """
#    {
#      "id":"7e3c069f-29ba-47d8-b287-5d69c3a73a99",
#      "ne-group-ids":["456bd5b8-eb12-3d7b-9ae8-11f3c9ab699a"]
#    }
#    """
#    And request req
#    And header Accept = 'application/json'
#    When method POST
#    Then status 201
#    And print response
#
#  Scenario: removeNodeFromVasPool
#    Given path '/acdcn/ui/vas/vaspool/remove'
#    * def req =
#    """
#      {
#        "name": "xxxxx",
#        "ac-system-name": "AC111",
#        "description": "",
#        "internal-interface-mode": "null",
#        "external-interface-mode": "null",
#        "res-allocated-policy": "random",
#        "networking-mode": "in-linedeployment",
#        "device-type": "unmanagedfw",
#        "device-quota": [ ]
#      }
#    """
#    And request req
#    And header Accept = 'application/json'
#    When method DELETE
#    Then status 201
#    And print response
#
#  Scenario: query vasNode by id
#    Given path '/acdcn/ui/vas/vaspool/queryTotalVasPools'
#    And header Accept = 'application/json'
#    When method GET
#    Then status 200
#    And print response
#
#  Scenario: Query all device response
#    Given path '/acdcn/ui/vas/vaspool/queryTotalVasPools'
#    And header Accept = 'application/json'
#    When method GET
#    Then status 200
#    And print response
#
#  Scenario: vas设备使用量查询接口
#    Given path '/acdcn/ui/vas/vaspool/queryTotalVasPools'
#    And header Accept = 'application/json'
#    When method GET
#    Then status 200
#    And print response
#
#  Scenario: vas设备资源配额管理
#    Given path '/acdcn/ui/vas/vaspool/queryTotalVasPools'
#    And header Accept = 'application/json'
#    When method GET
#    Then status 200
#    And print response
#
#  Scenario: 查询正常和异常状态的L4-L7
#    Given path '/acdcn/ui/vas/vaspool/queryTotalVasPools'
#    And header Accept = 'application/json'
#    When method GET
#    Then status 200
#    And print response
#
#
#
#
#
#
#
#
#
#  Scenario: 查询所有vaspool device
#    Given path '/acdcn/ui/vas/vaspool/queryAllDevice/'
#    And header Accept = 'application/json'
#    When method GET
#    Then status 200
#    And print response