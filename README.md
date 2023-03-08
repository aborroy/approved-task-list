# Alfresco Approved Task List

This add-on includes a new option `Approved files` in `My Profile` page. The new option shows a list of approved tasks by the current user, including a reference to the task, folder and documents.

The list can be filtered by year using the button `Filter`. When using the button `Export`, the list is downloaded as Excel file.

Users beloging to group `CONTROL_CALIDAD` have and additional button `User` to select a different user to build the list.

**Compatibility**

This add-on has been developed with Alfresco SDK 3.0.1 to be used with Alfresco 5.2 or later.

## Installing

The project includes following modules:

* `approved-task-list-repo.jar` to be deployed in Alfresco Repository, copying the file to `$ALFRESCO_HOME/modules/platform`
* `approved-task-list-share.jar` to be deployed in Alfresco Share, copying the file to `$ALFRESCO_HOME/modules/share`

## Building

Using default Maven command from module folder builds the JAR files.


```
$ mvn clean package
```