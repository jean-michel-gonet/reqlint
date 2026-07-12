Feature: The bit bucket works

  Background:

    Given Customer service has the following customers
      | CUSTOMER_ID | TOKEN_CAPACITY | REPLENISHMENT_RATE |
      | CUST_A      | 10             | 20                 |
      | CUST_B      | 20             | 10                 |

    And Now is 13:00

    And Application restarts

  Scenario: Upon first connection, a customer has his full token capacity available

    Then Customer "CUST_A" succeeds in consuming 8 tokens with response
      | CUSTOMER_ID | GRANTED_TOKENS | AVAILABLE_TOKENS |
      | CUST_A      | 8              | 2                |

    And Customer "CUST_B" succeeds in consuming 18 tokens with response
      | CUSTOMER_ID | GRANTED_TOKENS | AVAILABLE_TOKENS |
      | CUST_B      | 18             | 2                |

    When Customer "CUST_A" fails to consume 3 tokens with error
      | CUSTOMER_ID | TOKENS_TO_CONSUME | AVAILABLE_TOKENS |
      | CUST_A      | 3                 | 2                |