<?php
/*
Returns the abstract of one article (called by jQuery AJAX request)

by Nathan Moncrieff 08429995 and James Pyke 05090946
based off earlier work by Andrew Hood et al
*/

// see contents of main.php for logic tier access & templating functions
include("includes/main.php");

// stop invalid input
if(!isset($_GET['id'])) die;
$id = intval($_GET['id']);

// get results from logic tier
$article = soap_request('GetArticleDetails', array('articleId' => $id));

// output results
echo nl2br(readable($article[2]));

?>