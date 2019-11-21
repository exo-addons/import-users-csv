var csvData;

function exportData (url,fileName) {
    var a = document.createElement("a");
    document.body.appendChild(a);
    a.style = "display: none";
        a.href = url;
        a.download = fileName;
        a.click();
};

$(document).ready(function () {
    $('#files').on('change', handleFileSelect);
});

var DATATABLE_COLUMNS_CONFIGURATION = [
    { key: "userName", libelle: "User Name" },
    { key: "firstName", libelle: "First Name" },
    { key: "lastName", libelle: "Last Name" },
    { key: "email", libelle: "Email" },
    { key: "password", libelle: "password", "visible": false },
    { key: "groups", libelle: "Groups" },
    { key: "spaces", libelle: "Spaces" }];

function handleFileSelect(evt) {
    var files = evt.target.files; // FileList object
    var file = files[0];
    var reader = new FileReader();
    reader.readAsText(file);
    $('#nofileselect').text(file.name);
    reader.onload = function(event){

        var csv = event.target.result;
        csvData = $.csv.toObjects(csv);
        var csvHeaders = Object.keys(csvData[0]);

        listContact = new Array();
        if (csvData != "")
        {
            for (i = 0; i < csvData.length; i++)
            {
                entity = new Array();
                csvHeaders.forEach(function(header, index) {
                    entity[index] = csvData[i][header];
                });

                listContact.push(entity);
            }
        }

        var datatableColumns = provideDatatableColumns(csvHeaders);

        oTable = $("#multiTable").dataTable
        ({
            "bJQueryUI": true,
            "bProcessing": true,
            "sPaginationType": "full_numbers",
            "bRetrieve":true,
            "bFilter": false,
            "bLengthChange": false,
            "aaData": listContact,
            "aoColumns": datatableColumns
        });
        $("#uploadusers").show();
        $("#cancelupload").show();
    };
    reader.onerror = function(){ alert('Unable to read ' + file.fileName); };
}

var provideDatatableColumns = function(columnKeys) {

    var datatableColumns = [];

    for (var columnIndex = 0; columnIndex < columnKeys.length; columnIndex++) {
        var key = columnKeys[columnIndex];
        var dataTableConfiguration = findInDatatableConfiguration(key);

        if (dataTableConfiguration) {
            var visible = (dataTableConfiguration.hasOwnProperty("visible"))? dataTableConfiguration.visible: true;
            if (dataTableConfiguration.libelle) {
                datatableColumns.push({ "sTitle": dataTableConfiguration.libelle, "bVisible": visible });
            } else {
                datatableColumns.push({ "sTitle": dataTableConfiguration, "bVisible": visible });
            }
        }
    }

    return datatableColumns;
}

var findInDatatableConfiguration = function(key) {

    for (var configurationIndex = 0; configurationIndex < DATATABLE_COLUMNS_CONFIGURATION.length; configurationIndex++) {
        var dataTableConfiguration = DATATABLE_COLUMNS_CONFIGURATION[configurationIndex];

        if (dataTableConfiguration.key === key) {
            return dataTableConfiguration;
        }
    }

    return key;
}

var importUsers = function () {
    var createusers=$("#createusers").is(":checked");
    var addusers=$("#addusers").is(":checked");

    var body = [];

    for (var i=0; i<csvData.length; i++) {

        var user = csvData[i];
        var additionalInformations = {};
        for (let key of Object.keys(user)) {
            if(key!="userName" &&
                key!="firstName" &&
                key!="lastName" &&
                key!="email" &&
                key!="password" &&
                key!="groups" &&
                key!="spaces") {
                additionalInformations[key]=user[key];
            }
        }


        body.push({
            "userName": user["userName"],
            "firstName": user["firstName"],
            "lastName": user["lastName"],
            "email": user["email"],
            "password": user["password"],
            "groups": user["groups"],
            "spaces": user["spaces"],
            "additionalInformations": additionalInformations
        });
    }

    $.ajax
    ({
        beforeSend: function() {
            $("#AjaxLoadingMask").show();
        },
        cache: true,
        type: "POST",
        async: true,
        data: JSON.stringify(body),
        contentType: "application/json",
        dataType: "json",
        url: "/rest/private/importusersrest/importusers?creatduplicated="+createusers+"&addexistingusers="+addusers,
        statusCode: {
            400: function (xhr) {
                $("#actionfail").html('<i class="uiIconError"></i>' + xhr.responseText);
                $("#actionfail").show().delay(5000).fadeOut();
                $("#AjaxLoadingMask").hide();
            },
            500: function (xhr) {
                $("#actionfail").html('<i class="uiIconError"></i>' + xhr.responseText);
                $("#actionfail").show().delay(5000).fadeOut();
                $("#AjaxLoadingMask").hide();
            }
        }
    })
        .fail
    (
        function ()
        {
            $("#AjaxLoadingMask").hide();
        }
    )
        .done
    (
        function (data)
        {
            var today = new Date();
            var fileName = today.toISOString().substring(0, 10)+"-imported-users.csv";
            var urlreport = '/rest/private/importusersrest/getReport?reportId='+data.file;
            exportData(urlreport, fileName);
            $("#AjaxLoadingMask").hide();
            $("#actionsuccess").html('<i class="uiIconSuccess"></i>'+data.message);
            $("#actionsuccess").show().delay(5000).fadeOut();

        }
    );
}

var cancelMulti = function ()
{
    if((typeof oTable != "undefined")) {
        oTable.fnClearTable();
        oTable.fnDestroy();
        delete oTable;
    }
    $("#multiTable thead").remove();
    $("#uploadusers").hide();
    $("#cancelupload").hide();
    $("#files").val("");
    $("#actionsuccess").hide();
}



