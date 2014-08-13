<?php
/*
Results for recommendations related to an article

by Nathan Moncrieff 08429995 and James Pyke 05090946
based off earlier work by Andrew Hood et al
*/

// see contents of main.php for logic tier access & templating functions
include("includes/main.php");

// stop invalid input
if (!isset($_GET['id'])) die;
$id = intval($_GET['id']);

// initial templating
$article = soap_request('GetArticleDetails', array('articleId' => $id));
$eprints_title = readable($article[1]);
$eprints_head = '<script src="javascript/jquery-1.9.1.min.js"></script>	
		<script src="javascript/search.js"></script>';
$eprints_content = '<h1>Recommended articles for "' . readable($article[1]) . '"</h1>';

// get results from logic tier
$articles = soap_request('RecommendByArticle', array(
		'articleId' => $id,
		'topN' => 10,
	));
	
// apply template to results and return whole page
$eprints_content .= template_search($articles);
template_main();

?>