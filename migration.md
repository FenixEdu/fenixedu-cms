# 2.x.x -> 3.x.x

## RoleTemplate atributes

``` ALTER TABLE ROLE_TEMPLATE CHANGE `DESCRIPTION` `NAME` text;
``` ALTER TABLE ROLE CHANGE `OID_GROUP` `OID_PERSISTENT_GROUP` bigint(20) unsigned;


