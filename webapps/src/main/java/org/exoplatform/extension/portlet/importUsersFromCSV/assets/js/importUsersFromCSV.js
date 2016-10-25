var cvsData;

$(document).ready(function () {
    $('#files').on('change', handleFileSelect);
});

function cvsData(evt) {
    var files = evt.target.files; // FileList object
    var file = files[0];
    var reader = new FileReader();
    reader.readAsText(file);
    reader.onload = function(event){
        var csv = event.target.result;
        csv = res;
        cvsData = $.csv.toObjects(csv);
        listContact = new Array();
        if (cvsData != "")
        {
            for (i = 0; i < cvsData.length; i++)
            {
                entity = new Array();
                entity[0] = cvsData[i].userName;
                entity[1] = cvsData[i].firstName;
                entity[2] = cvsData[i].lastName;
                entity[3] = cvsData[i].email;
                entity[4] = cvsData[i].password;
                listContact.push(entity);
            }
        }
        oTable = $("#multiTable").dataTable
        ({
            "bJQueryUI": true,
            "bProcessing": true,
            "sPaginationType": "full_numbers",
            "bRetrieve":true,
            "bFilter": false,
            "bLengthChange": false,
            "oLanguage": {
                "sEmptyTable": "No users available in file",
                "sInfo": "Showing _START_ to _END_ of _TOTAL_ Users",
                "sInfoEmpty": "No Users to show",
                "sLengthMenu": "Show _MENU_ Users",
                "sZeroRecords": "No Users to display"
            },
            "aaData": listContact,
            "aoColumns":
                [
                    {
                        "sTitle": "User Name"
                    },
                    {
                        "sTitle": "First Name"
                    },
                    {
                        "sTitle": "Last Name"
                    },
                    {
                        "sTitle": "email"
                    },
                    {
                        "sTitle": "password",
                        "bVisible": false
                    }
                ]
        });

        $("#uploadusers").show();
        $("#cancelupload").show();
    };
    reader.onerror = function(){ alert('Unable to read ' + file.fileName); };
}

function handleFileSelect(evt) {
    var files = evt.target.files; // FileList object
    var file = files[0];
    var reader = new FileReader();
    reader.readAsText(file);
    $('#nofileselect').text(file.name);
    reader.onload = function(event){
        var csv = event.target.result;
        cvsData = $.csv.toObjects(csv);
        listContact = new Array();
        if (cvsData != "")
        {
            for (i = 0; i < cvsData.length; i++)
            {
                entity = new Array();
                entity[0] = cvsData[i].userName;
                entity[1] = cvsData[i].firstName;
                entity[2] = cvsData[i].lastName;
                entity[3] = cvsData[i].email;
                entity[4] = cvsData[i].password;
                entity[5] = cvsData[i].groups;
                entity[6] = cvsData[i].spaces;
                listContact.push(entity);
            }
        }
        oTable = $("#multiTable").dataTable
        ({
            "bJQueryUI": true,
            "bProcessing": true,
            "sPaginationType": "full_numbers",
            "bRetrieve":true,
            "bFilter": false,
            "bLengthChange": false,
            "aaData": listContact,
            "aoColumns":
                [
                    {
                        "sTitle": "User Name"
                    },
                    {
                        "sTitle": "First Name"
                    },
                    {
                        "sTitle": "Last Name"
                    },
                    {
                        "sTitle": "Email"
                    },
                    {
                        "sTitle": "password",
                        "bVisible": false
                    },
                    {
                        "sTitle": "Groups"
                    },
                    {
                        "sTitle": "Spaces"
                    }
                ]
        });
        $("#uploadusers").show();
        $("#cancelupload").show();
    };
    reader.onerror = function(){ alert('Unable to read ' + file.fileName); };
}

var importUsers = function ()
{
    var createusers=$("#createusers").is(":checked");
    var addusers=$("#addusers").is(":checked");
    $.ajax
    ({
        beforeSend: function() {
            $("#AjaxLoadingMask").show();
        },
        cache: true,
        type: "POST",
        async: true,
        data: JSON.stringify(cvsData),
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



