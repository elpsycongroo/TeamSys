jQuery(function($) {
	jQuery(".sidebar active").removeClass("active");
	var a = $("#navigation").attr("data");
	jQuery("#"+a).addClass("active");
	jQuery("#"+a).parent().parent().addClass("active open");
});