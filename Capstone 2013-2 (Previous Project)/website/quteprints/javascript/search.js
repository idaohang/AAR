/*
AJAX code for search results pages (used in category.php, search.php, recommendations.php)

by Nathan Moncrieff 08429995 and James Pyke 05090946
*/

$(function() {

	$('.btn_abs').click(function() {
		var $abstract = $(this).parent().find('.abstract');
		//alert($(this).parent().attr('eprintid'));
		if($abstract.is(':visible')) {
			$abstract.slideUp();
		} else {
			$abstract.slideDown();
			if(!$abstract.find('.abstract_contents').is(':visible')) {
				$abstract.find('.abstract_contents').load('ajax_abstract.php?id=' + $(this).parent().attr('eprintid'), function() {
					$(this).parent().find('.abstract_throbber').hide();
					$(this).slideDown();					
				});
			}
		}
		return false;
	});
	
	$('.btn_rec').click(function() {
		document.location.href = 'recommendations.php?id=' + $(this).parent().attr('eprintid');
	});

});