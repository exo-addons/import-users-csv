$.fn.dataTableExt.aoFeatures.push(
{
    fnInit: function(settings)
    {
        var $searchSpan = $("<span>",
        {
            class: "im-search",
            style: "position: absolute; top: 0; left: 0; height: 24px; width: 20%; margin-left: 12px; margin-top: 5px;",
        });

        var $searchInput = $("<input>",
        {
            class: "im-search-input ui-corner-all ui-widget ui-widget-content",
            style: "height: 100%; width: 100%;",
        })
        .appendTo($searchSpan)
        .on("keyup", function()
        {
            settings.oInstance.fnFilter($searchInput.val());
        });

        var $searchIcon = $("<a>",
        {
            tabIndex: -1,
        })
        .appendTo($searchSpan)
        .button(
        {
            icons: { primary: "ui-icon-search" },
            text: false
        })
        .removeClass("ui-corner-all")
        .addClass("ui-corner-right ui-combobox-toggle")
        .css(
        {
            "border-bottom": 0,
            "border-right": 0,
            "border-top": 0,
            "bottom": "1px",
            "margin-left": "-1px",
            "padding": 0,
            "position": "absolute",
            "right": "-1px",
            "top": "1px",
            "width": "16px",
        });

        return $searchSpan[0];
    },
    "cFeature": "Z",
    "sFeature": "Filter",
});