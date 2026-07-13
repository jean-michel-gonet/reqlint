Feature: The bit bucket works

  Background:

    Given Customer service has the following customers
      | CUSTOMER_ID | TOKEN_CAPACITY | REPLENISHMENT_RATE |
      | CUST_A      | 10             | 20                 |
      | CUST_B      | 20             | 10                 |

    And Now is 13:00

  Scenario: @TC_101 Upon first connection, a customer has his full token capacity available
    * Stage 1 - "Check token availability of a customer"

    Then Customer "CUST_A" has the following token availability
      | CUSTOMER_ID | AVAILABLE_TOKENS | REPLENISHMENT_RATE | TIME_STAMP |
      | CUST_A      | 10               | 20                 | 13:00      |

  Scenario: @TC_102 Each customer has its own bucket
    * Stage 1 - "Obtain the bucket capacity of one customer"

    Then Customer "CUST_A" has the following token availability
      | CUSTOMER_ID | AVAILABLE_TOKENS | REPLENISHMENT_RATE | TIME_STAMP |
      | CUST_A      | 10               | 20                 | 13:00      |

    * Stage 2 - "Compare the bucket capacity of a different customer"
    And Customer "CUST_B" has the following token availability
      | CUSTOMER_ID | AVAILABLE_TOKENS | REPLENISHMENT_RATE | TIME_STAMP |
      | CUST_B      | 20               | 10                 | 13:00      |

  Scenario:@TC_201 Bucket gets replenished as time passes
    * Stage 1 - "Consume some tokens."
    Then Customer "CUST_A" succeeds in consuming 8 tokens with response
      | CUSTOMER_ID | GRANTED_TOKENS | AVAILABLE_TOKENS |
      | CUST_A      | 8              | 2                |

    * Stage 2 - "Verify that token availability increased according to replenishment rate"
    When Now is 13:00:00.200

    Then Customer "CUST_A" has the following token availability
      | CUSTOMER_ID | AVAILABLE_TOKENS | REPLENISHMENT_RATE | TIME_STAMP   |
      | CUST_A      | 6                | 20                 | 13:00:00.200 |

    When Now is 13:00:00.300

    Then Customer "CUST_A" has the following token availability
      | CUSTOMER_ID | AVAILABLE_TOKENS | REPLENISHMENT_RATE | TIME_STAMP   |
      | CUST_A      | 8                | 20                 | 13:00:00.300 |

    When Now is 13:00:00.400

    Then Customer "CUST_A" has the following token availability
      | CUSTOMER_ID | AVAILABLE_TOKENS | REPLENISHMENT_RATE | TIME_STAMP   |
      | CUST_A      | 10               | 20                 | 13:00:00.400 |

  Scenario:@TC_202 Bucket does not get replenished beyond capacity

    * Stage 1 - "Consume some tokens."
    Then Customer "CUST_A" succeeds in consuming 8 tokens with response
      | CUSTOMER_ID | GRANTED_TOKENS | AVAILABLE_TOKENS |
      | CUST_A      | 8              | 2                |

    * Stage 2 - "Verify that token availability maxes at capacity"
    When Now is 13:01

    Then Customer "CUST_A" has the following token availability
      | CUSTOMER_ID | AVAILABLE_TOKENS | REPLENISHMENT_RATE | TIME_STAMP |
      | CUST_A      | 10               | 20                 | 13:01      |

  Scenario:@TC_301 Bucket does not get replenished beyond capacity

    * Stage 1 - "Can consume some tokens."
    Then Customer "CUST_A" succeeds in consuming 8 tokens with response
      | CUSTOMER_ID | GRANTED_TOKENS | AVAILABLE_TOKENS |
      | CUST_A      | 8              | 2                |

    And Customer "CUST_A" succeeds in consuming 2 tokens with response
      | CUSTOMER_ID | GRANTED_TOKENS | AVAILABLE_TOKENS |
      | CUST_A      | 2              | 0                |

    * Stage 2 - "Receives an error when consuming too many tokens"
    When Customer "CUST_A" fails to consume 1 tokens with error
      | CUSTOMER_ID | TOKENS_TO_CONSUME | AVAILABLE_TOKENS |
      | CUST_A      | 1                 | 0                |
