<?php
/*
Front page with text search box and category list

by Nathan Moncrieff 08429995 and James Pyke 05090946
based off earlier work by Andrew Hood et al
*/

// see contents of main.php for logic tier access & templating functions
include("includes/main.php");

// read input
$id = isset($_GET['id']) ? intval($_GET['id']) : 0;

// initial templating
$eprints_head = '<script src="javascript/jquery-1.9.1.min.js"></script>
		<script src="javascript/browse.js"></script>';

// get results from logic tier
$categories = soap_request('GetCategories', array('categoryId' => $id));

// apply template to results
$results_formatted = '';
foreach ($categories as $category) {
	$results_formatted .= template('category', array(
			'ID' => $category->item[0],
			'TITLE' => readable($category->item[1]),
			'BUTTONCLASS' => (($category->item[2]) ? 'btn_expand' : 'btn_expand_disabled')
		)) . "\n";
}

// apply template to whole page
$eprints_content = template('categories', array('RESULTS' => $results_formatted));
template_main();

?>