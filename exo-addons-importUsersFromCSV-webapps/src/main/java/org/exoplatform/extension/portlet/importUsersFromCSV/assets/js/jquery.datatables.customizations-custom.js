// http://datatables.net/plug-ins/api#fnFilterOnReturn
jQuery.fn.dataTableExt.oApi.fnFilterOnReturn = function(oSettings)
{
    var _that = this;
  
    this.each(function(index)
    {
        $.fn.dataTableExt.iApiIndex = index;
        var $this = this;
        var anControl = $('input', _that.fnSettings().aanFeatures.f);
        anControl.unbind('keyup').bind('keypress', function(event)
        {
            if (event.which == 13)
            {
                $.fn.dataTableExt.iApiIndex = index;
                _that.fnFilter(anControl.val());
            }
        });

        return this;
    });
    
    return this;
};