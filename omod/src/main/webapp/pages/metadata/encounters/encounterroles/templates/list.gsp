<button ui-sref="edit">${ ui.message("adminui.addNewEncounterRole.title") }</button>
<br/>
<br/>

<table>
    <thead>
    <tr>
        <th>${ui.message('general.name')}</th>
        <th>${ui.message('general.description')}</th>
        <th class="adminui-action-column">${ui.message('general.action')}</th>
    </tr>
    </thead>
    <tbody>
    <tr ng-repeat="encounterRole in encounterRoles">
        <td ng-class="{ retired: encounterRole.retired }">{{encounterRole.name}}</td>
        <td ng-class="{ retired: encounterRole.retired }">{{encounterRole.description}}</td>
        <td>
            <a ng-hide="encounterRole.retired" ui-sref="edit({encounterRoleUuid: encounterRole.uuid})">
                <i class="icon-pencil edit-action" title="${ui.message("emr.edit")}"></i>
            </a>
            <a ng-hide="encounterRole.retired" ng-click="retire(encounterRole)">
                <i class="icon-remove delete-action" title="${ui.message("emr.delete")}"></i>
            </a>
            <a ng-show="encounterRole.retired" ng-click="unretire(encounterRole)">
                <i class="icon-reply edit-action" title="${ui.message("general.restore")}"></i>
            </a>
            <a ng-click="purge(encounterRole)" class="right">
                <i class="icon-trash delete-action" title="${ui.message("general.purge")}"></i>
            </a>
        </td>
    </tr>
    </tbody>
</table>