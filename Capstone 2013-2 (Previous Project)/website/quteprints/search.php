<?php
/*
Results for text search

by Nathan Moncrieff 08429995 and James Pyke 05090946
based off earlier work by Andrew Hood et al
*/

// see contents of main.php for logic tier access & templating functions
include("includes/main.php");

// stop invalid input
if (!isset($_GET['q'])) die;
$term = $_GET['q'];

// initial templating
$eprints_title = htmlentities($term);
$eprints_head = '<script src="javascript/jquery-1.9.1.min.js"></script>	
		<script src="javascript/search.js"></script>';
$eprints_content = '<h1>Search results for "' . htmlentities($term) . '"</h1>';

// get results from logic tier
$articles = soap_request('GetArticlesBySearchTerm', array(
		'term' => $term,
		'topN' => 10,
	));
	
// apply template to results and return whole page
$eprints_content .= template_search($articles);
template_main();

?>