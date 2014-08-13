<?php
/*
Results for browsing by category

by Nathan Moncrieff 08429995 and James Pyke 05090946
based off earlier work by Andrew Hood et al
*/

// see contents of main.php for logic tier access & templating functions
include("includes/main.php");

// stop invalid input
if (!isset($_GET['id'])) die;
$id = intval($_GET['id']);

// initial templating
$eprints_head = '<script src="javascript/jquery-1.9.1.min.js"></script>	
		<script src="javascript/search.js"></script>';
$eprints_content = '';

// get results from logic tier
$articles = soap_request('GetArticlesByCategory', array(
		'category' => $id,
		'topN' => 10,
	));

// remove title from results (if any results) and apply templating
if ($articles && !is_array($articles[0]->item)) {
	$eprints_title = readable($articles[0]->item);
	$eprints_content .= '<h1>' . readable($articles[0]->item) . '</h1>';	
	unset($articles[0]);
}

// apply template to rest of results and return whole page
$eprints_content .= template_search($articles);
template_main();

?>