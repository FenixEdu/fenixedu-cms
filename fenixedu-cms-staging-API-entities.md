#Site
* Viewer
    * id
    * slug
    * name (LocalizedString)
    * description (LocalizedString)
    * alternativeSite
    * creationDate(DateTime)
    * published
    * embedded
    * analyticsCode
    * theme (externalId)
    * createdBy (username)
* Create
    * name (LocalizedString)
    * description (LocalizedString)
    * theme (externalId) [optional]
    * embedded [optional]
    * template [optional]
* Edit
    * slug
    * name (LocalizedString)
    * description (LocalizedString)
    * analyticsCode
    * theme (externalId)
    * alternativeSite
    * published

#Page
* Viewer
    * id
    * creationDate(DateTime)
    * modificationDate(DateTime)
    * published
    * site (externalId)
    * createdBy (username)
    * name (LocalizedString)
    * slug
* Create
    * name (LocalizedString) [optional]
    * slug [optional]
    * published [optional]
* Edit
    * slug
    * name (LocalizedString)
    * published

#Post
* Viewer
    * id
    * name (LocalizedString)
    * body (LocalizedString)
    * creationDate(DateTime)
    * modificationDate(DateTime)
    * publicationBegin(DateTime)
    * publicationEnd(DateTime)
    * createdBy (username)
    * site (externalId)
    * slug
    * published {post.active}
* Create
    * name (LocalizedString) [optional]
    * slug [optional]
    * body (LocalizedString) [optional]
* Edit
    * slug
    * name (LocalizedString)
    * body (LocalizedString)
    * published (active)
    * publicationBegin (DateTime)
    * publicationEnd (DateTime)

#PostRevision
* Viewer
    * id
    * body (LocalizedString)
    * createdBy (username)
    * next (externalId)
    * previous (externalId)
    * post (externalId)
    * revisionDate (DateTime)

#PostFile
* Viewer
    * id
    * index
    * isEmbedded
    * post (externalId)
    * checksum
    * contentType
    * displayName
    * filename
    * size
    * accessgroup (presentationName)
    * url
* Create
    * ???????
* Edit
    * index
    * isEmbedded
    * accessGroup

#Category
* Viewer
    * id
    * slug
    * creationDate(DateTime)
    * name (LocalizedString)
    * createdBy (username)
    * site (externalId)
* Create
    * name (LocalizedString) [optional]
    * slug [optional]
* Edit
    * slug
    * name (LocalizedString)

#Menu
* Viewer
    * id
    * slug
    * topMenu
    * creationDate(DateTime)
    * name (LocalizedString)
    * createdBy (username)
    * site (externalId)
    * menuItems (Array - externalId)
* Create
    * name (LocalizedString) [optional]
    * slug [optional]
    * topMenu [optional]
* Edit
    * slug (LocalizedString)
    * name (LocalizedString)

#MenuItem
* Viewer
    * id
    * creationDate(DateTime)
    * name (LocalizedString)
    * position
    * url
    * folder
    * createdBy (username)
    * menu (externalId)
    * children (jsonArray - externalId)
* Create
    * name (LocalizedString) [optional]
    * position [optional]
    * folder [optional]
    * url [optional]
* Edit
    * position
    * name (LocalizedString)
    * url
    * folder

#Theme
* Viewer
    * id
    * createdBy (username)
    * creationDate(DateTime)
    * name (LocalizedString)
    * description
    * type

#Folder
* Viewer
   * id
   * path [optional]
   * description (LocalizedString) [optional]
   * custom

#Template/CMSTemplate
* Viewer 
   * type
   * name
   * description

#Component
* Viewer
   * # delegated on descriptor
