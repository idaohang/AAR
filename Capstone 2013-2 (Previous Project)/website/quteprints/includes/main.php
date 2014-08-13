<?php
/*
Common functions (database access, templating etc)

by Nathan Moncrieff 08429995 and James Pyke 05090946
based off earlier work by Andrew Hood et al
*/

// fix character encoding
header('Content-Type: text/html; charset=utf-8');

// sets up the connection to the logic tier
// edit to adjust server settings (point to the WSDL file)
$client = new SoapClient("http://localhost:8080/RecommendWS/RecommendWS?WSDL");

// make request to logic tier
function soap_request($name, $params) {
	global $client;

	// run soap request
	$results = (array)$client->$name($params);
	
	// if no data, return null
	if (!isset($results['return'])) {
		return null;
	}
	
	// fix problem where code expects array but single result returns a scalar
	if (sizeof($results['return']) == 1) $results['return'] = array($results['return']);
	
	return $results['return'];
}

// fix unicode errors etc
function readable($string) {
	$string = str_replace('â€¦', '…', $string);
	$string = str_replace('â€œ', '“', $string);
	$string = str_replace('â€ ', '”', $string);
	$string = str_replace('â€™', '’', $string);
	$string = str_replace('â€˜', '‘', $string);
	$string = str_replace('â€”', '–', $string);
	$string = str_replace('â€“', '—', $string);
	$string = str_replace('â€¢', '-', $string);
	return htmlentities(html_entity_decode($string));
}

// templating system
function template($templatename, $fields = array()) {
	global $cachedtemplates;

	// cache template file if it hasn't been already
	if (!isset($cachedtemplates[$templatename])) {
		$cachedtemplates[$templatename] = file_get_contents('includes/template/' . preg_replace('@[./\\"]@', '', $templatename) . '.html');
	}
	
	// read from cache
	$output = $cachedtemplates[$templatename];
	
	// replace placeholders with real content
	foreach($fields as $token=>$content) {
		$output = str_replace('<!--' . $token . '-->', $content, $output);
	}
	
	return $output;
}

// main template for every page
function template_main() {
	global $eprints_title, $eprints_head, $eprints_content;
	
	// set strings as blank if they haven't been set yet
	$eprints_title = isset($eprints_title) ? $eprints_title . ' | ' : '';
	$eprints_head = isset($eprints_head) ? $eprints_head : '';
	$eprints_content = isset($eprints_content) ? $eprints_content : '';

	// run template
	echo template('main', array(
			'TITLE'=>$eprints_title,
			'HEAD'=>$eprints_head,
			'CONTENT'=>$eprints_content));
}

// template for article lists, common to category.php, search.php, recommendations.php
function template_search($articles) {
	
	// no results
	if (!$articles) {
		return template('searchresult-none');
	}
	
	// has results
	$template = '';
	foreach ($articles as $article) {
		$template .= template('searchresult', array(
			'TITLE' => readable($article->item[1]),
			'ID' => $article->item[0]
		));
	}
	return $template;
}

?>