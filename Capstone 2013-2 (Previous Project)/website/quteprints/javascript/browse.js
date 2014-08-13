/*
AJAX code for category list (used in index.php)

by Nathan Moncrieff 08429995 and James Pyke 05090946
*/

$(function() {

	var expand = function() {
		var $expand = $(this).parent().find('.expand').first();
		if($expand.is(':visible')) {
			$expand.slideUp();
		} else {
			$expand.slideDown();
			if(!$expand.find('.expand_contents').first().is(':visible')) {
				$expand.find('.expand_contents').first().load('index.php?id=' + $(this).parent().attr('catid') + ' #results', function() {
					$expand.find('.expand_contents').find('.btn_expand').click(expand);
					$(this).parent().find('.expand_throbber').first().hide();
					$(this).slideDown();
				});
			}
		}
		return false;
	}

	$('.btn_expand').click(expand);

});